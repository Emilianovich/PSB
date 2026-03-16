package ptystudybuddy.psb.inscriptions

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import java.time.LocalDateTime
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import ptystudybuddy.psb.bucket.BucketService
import ptystudybuddy.psb.email.SendGridService
import ptystudybuddy.psb.entities.InscriptionId
import ptystudybuddy.psb.entities.InscriptionRes
import ptystudybuddy.psb.entities.InscriptionsEntity
import ptystudybuddy.psb.entities.SessionStatus
import ptystudybuddy.psb.entities.toInscriptionRes
import ptystudybuddy.psb.exceptions.customs.UnprocessableEntityException
import ptystudybuddy.psb.helpers.AuthHelper
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.AvailabilityRepository
import ptystudybuddy.psb.repositories.InscriptionsRepository
import ptystudybuddy.psb.repositories.SessionAssignmentRepository
import ptystudybuddy.psb.repositories.SessionsRepository
import ptystudybuddy.psb.repositories.StudentsRepository

@Service
class InscriptionService(
  private val studentsRepository: StudentsRepository,
  private val inscriptionsRepository: InscriptionsRepository,
  private val authHelper: AuthHelper,
  private val jdbcTemplate: JdbcTemplate,
  private val sessionsRepository: SessionsRepository,
  private val availabilityRepository: AvailabilityRepository,
  private val sendGridService: SendGridService,
  private val sessionAssignmentRepository: SessionAssignmentRepository,
  private val bucketService: BucketService,
) {
  @Transactional
  fun create(req: CreateInscriptionReq): ResponseEntity<SuccessRes<String>> {
    val student =
      studentsRepository.findByIdOrNull(authHelper.userId())
        ?: throw EntityNotFoundException("El estudiante no existe")
    val session =
      sessionsRepository.findByIdOrNull(req.sessionId).takeUnless { it == null }
        ?: throw EntityNotFoundException("La sesión solicitada no existe")
    val availability =
      availabilityRepository.findByIdOrNull(session.availabilityId.id.toString())
        ?: throw EntityNotFoundException("Disponibilidad no existe")
    val assignedSession =
      sessionAssignmentRepository.findBySessionId(session)
        ?: throw EntityNotFoundException("La sesión todavía no ha sido asignada")
    val studentPendingSessions =
      jdbcTemplate.queryForObject(
        """
              SELECT COUNT(*)
                FROM availability av
                INNER JOIN sessions s ON av.id = s.availability_id
                INNER JOIN inscriptions ins ON s.id = ins.session_id
                WHERE av.date = ?
                AND av.schedule_id = ?
                AND ins.student_id = ?
                """,
        Int::class.java,
        availability.date,
        availability.scheduleId.id,
        student.id,
      )
    studentPendingSessions?.takeUnless { it > 0 }
      ?: throw UnprocessableEntityException(
        "${student.fullname}, ya tienes una sesión programada a esa hora"
      )
    inscriptionsRepository.save(
      InscriptionsEntity(
        InscriptionId(studentId = student.id.toString(), sessionId = session.id.toString()),
        studentId = student,
        sessionId = session,
      )
    )
    sendGridService.sendEmailAfterInscription(
      fullName = student.fullname,
      studentEmail = student.email,
      subjectName = assignedSession.subjectId.name,
      sessionStartTime = availability.scheduleId.startTime,
      sessionEndTime = availability.scheduleId.endTime,
      sessionDate = availability.endDatetime as LocalDateTime,
      tutorName = assignedSession.tutorId.fullname,
    )
    return ResponseEntity.status(HttpStatus.CREATED.value())
      .body(
        SuccessRes(
          statusCode = HttpStatus.CREATED.value(),
          content = "Inscripción realizada exitosamente",
        )
      )
  }

  @Transactional
  fun markAttendance(
    sessionId: String,
    req: List<MarkAttendanceReq>,
  ): ResponseEntity<SuccessRes<String>> {
    val session =
      sessionsRepository.findByIdOrNull(sessionId)
        ?: throw EntityNotFoundException("La sesión solicitada no existe")
    val sessionAssigned = sessionAssignmentRepository.findBySessionId(session)
    if (authHelper.userId() != sessionAssigned?.tutorId?.id) {
      throw IllegalArgumentException("Esta sesión no le pertenece")
    }

    if (LocalDateTime.now().isBefore(session.endDatetime)) {
      throw IllegalArgumentException("No puede evaluar una sesión que no ha terminado")
    }
    // NOTE in case frontend sends an empty list by mistake, validation of non attendance is still
    // present
    val filteredList = req.filter { it.studentId.trim().isNotEmpty() }
    // NOTE this is technically possible
    if (filteredList.isEmpty()) {
      session.attendanceMarked = true
      // TODO Change after Created SP to change session to not active when comparing date and time
      session.status = SessionStatus.NOT_ACTIVE
      val nonAttendanceList = inscriptionsRepository.findBySessionId(session)
      if (nonAttendanceList.isNotEmpty()) {
        nonAttendanceList.forEach { it.assisted = false }
      }
      return ResponseEntity.ok(
        SuccessRes(
          statusCode = HttpStatus.OK.value(),
          content = "Lamentamos que ningún estudiante haya asistido :(",
        )
      )
    }
    val studentList = filteredList.map { it.studentId }
    val listOfValidInscriptions =
      inscriptionsRepository.findStudentsBySessionId(sessionId, studentList).onEach {
        it.assisted = true
      }
    session.attendanceMarked = true
    // TODO Change after Created SP to change session to not active when comparing date and time
    session.status = SessionStatus.NOT_ACTIVE
    inscriptionsRepository.saveAll(listOfValidInscriptions)
    return ResponseEntity.ok(
      SuccessRes(
        statusCode = HttpStatus.OK.value(),
        content = "Asistencia registrada para los estudiantes",
      )
    )
  }

  fun unsubscribe(sessionId: String): ResponseEntity<SuccessRes<String>> {

    val currentUserId = authHelper.userId()
    val currentDate = LocalDateTime.now()
    val session =
      sessionsRepository.findById(sessionId).orElseThrow {
        EntityNotFoundException("No se encontró una sesión con este id: $sessionId ")
      }

    val inscription =
      session.inscriptions.firstOrNull {
        it.sessionId.id == sessionId && it.studentId.id == currentUserId
      } ?: throw DataIntegrityViolationException("No estas inscrito en esta sesión")

    session.endDatetime.takeIf { it >= currentDate }
      ?: throw DataIntegrityViolationException("No te puedes desinscribir, la sesión ya comenzó ")

    inscriptionsRepository.deleteById(inscription.id)

    sendGridService.sendEmailAfterInscriptionCancel(
      studentEmail = inscription.studentId.email,
      subjectName =
        session.sessionsAssignment.first { it.sessionId.id == sessionId }.subjectId.name,
      sessionDate = session.endDatetime,
      tutorName = session.sessionsAssignment.first { it.sessionId.id == sessionId }.tutorId.fullname,
    )

    return ResponseEntity.ok(
      SuccessRes(
        statusCode = HttpStatus.OK.value(),
        content = "Se ha retirado su inscripción de la sesión",
      )
    )
  }

  fun getAllInscriptionsPerSession(
    sessionId: String
  ): ResponseEntity<SuccessRes<List<InscriptionRes>>> {
    val session =
      sessionsRepository.findByIdOrNull(sessionId)
        ?: throw EntityNotFoundException("No se encontró la sesión buscada")
    val rawInscriptions = inscriptionsRepository.findBySessionId(session)
    val inscriptions = rawInscriptions.map { it.toInscriptionRes() }
    inscriptions.onEach { it.studentPicture = bucketService.getSignedUrl(it.studentPicture) }
    return ResponseEntity.ok(SuccessRes(statusCode = HttpStatus.OK.value(), content = inscriptions))
  }
}

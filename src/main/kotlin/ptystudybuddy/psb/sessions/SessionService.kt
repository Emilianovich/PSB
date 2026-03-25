package ptystudybuddy.psb.sessions

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import ptystudybuddy.psb.bucket.BucketService
import ptystudybuddy.psb.email.SendGridService
import ptystudybuddy.psb.entities.SessionAssignmentEntity
import ptystudybuddy.psb.entities.SessionAssignmentId
import ptystudybuddy.psb.entities.SessionStatus
import ptystudybuddy.psb.entities.SessionSummaryRes
import ptystudybuddy.psb.entities.SessionsEntity
import ptystudybuddy.psb.entities.StudentSessionsRes
import ptystudybuddy.psb.entities.toSessionSummaryRes
import ptystudybuddy.psb.entities.toStudentSessionsRes
import ptystudybuddy.psb.exceptions.customs.UnprocessableEntityException
import ptystudybuddy.psb.helpers.AuthHelper
import ptystudybuddy.psb.helpers.DateAndTimeHelper
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.AvailabilityRepository
import ptystudybuddy.psb.repositories.InscriptionsRepository
import ptystudybuddy.psb.repositories.SessionAssignmentRepository
import ptystudybuddy.psb.repositories.SessionSummaryRepository
import ptystudybuddy.psb.repositories.SessionsRepository
import ptystudybuddy.psb.repositories.StudentSessionsRepository
import ptystudybuddy.psb.repositories.SubjectsRepository
import ptystudybuddy.psb.repositories.TutorsRepository

@Service
class SessionService(
  private val sessionsRepository: SessionsRepository,
  private val availabilityRepository: AvailabilityRepository,
  private val subjectsRepository: SubjectsRepository,
  private val tutorsRepository: TutorsRepository,
  private val sessionAssignmentRepository: SessionAssignmentRepository,
  private val authHelper: AuthHelper,
  private val jdbcTemplate: JdbcTemplate,
  private val sessionSummaryView: SessionSummaryRepository,
  private val studentSessions: StudentSessionsRepository,
  private val studentSessionFilter: StudentSessionFilter,
  private val bucketService: BucketService,
  private val inscriptionsRepository: InscriptionsRepository,
  private val sendGridService: SendGridService,
) {
  // NOTE finding a specific session when student wants to enroll
  fun getOneSession(id: String): ResponseEntity<SuccessRes<SessionsForInscriptionRes>> {
    val requestedSession =
      sessionsRepository.findByIdOrNull(id)
        ?: throw EntityNotFoundException("La sesión buscada no existe")
    val subjectId =
      sessionAssignmentRepository.findBySessionId(requestedSession)?.subjectId?.id
        ?: throw UnprocessableEntityException("La materia buscada no existe")
    val sessions =
      getListOfSessionsBySubjectId(subjectId).takeUnless { it.isEmpty() }
        ?: throw EntityNotFoundException("No se encontró la sesión especificada para esta materia")
    val session =
      sessions.find { it.sessionId == requestedSession.id }
        ?: throw EntityNotFoundException("No se encontró la sesión especificada para esta materia")
    return ResponseEntity.ok(SuccessRes(statusCode = HttpStatus.OK.value(), content = session))
  }

  // NOTE For admin is to find session per day and tutors to see their past and pending sessions
  fun filterSessions(req: SessionFiltersReq): ResponseEntity<SuccessRes<List<SessionSummaryRes>>> {
    val query = SessionFilter.getSessions(authHelper.userRole(), req, authHelper.userId())
    val rawSessions = sessionSummaryView.findAll(query)
    rawSessions.takeUnless { it.isEmpty() }
      ?: throw EntityNotFoundException("No se encontró sesiones que cumplan lo solicitado")
    val sessions = rawSessions.map { it.toSessionSummaryRes() }
    sessions.onEach { session ->
      session.tutorPicture = bucketService.getSignedUrl(session.tutorPicture)
    }
    return ResponseEntity.ok()
      .body(SuccessRes(statusCode = HttpStatus.OK.value(), content = sessions))
  }

  // NOTE this function is for finding pending and past student sessions
  fun handleStudentFilter(
    req: StudentSessionFiltersReq
  ): ResponseEntity<SuccessRes<List<StudentSessionsRes>>> {
    val query = studentSessionFilter.getSessions(req, authHelper.userId())
    val sessions = studentSessions.findAll(query)
    if (req.status == "NOT_ACTIVE" && sessions.isEmpty()) {
      throw EntityNotFoundException("No tiene sesiones anteriores")
    }
    if (sessions.isEmpty()) {
      throw EntityNotFoundException("No tiene sesiones pendientes")
    }
    val formatedSessions = sessions.map { it.toStudentSessionsRes() }
    formatedSessions.onEach { session ->
      session.tutorPicture = bucketService.getSignedUrl(session.tutorPicture)
    }
    return ResponseEntity.ok()
      .body(SuccessRes(statusCode = HttpStatus.OK.value(), content = formatedSessions))
  }

  @Transactional
  fun createSession(req: CreateSession): ResponseEntity<SuccessRes<String>> {
    val availability =
      availabilityRepository.findByIdOrNull(req.availabilityId)
        ?: throw EntityNotFoundException("No se encontró la disponibilidad requerida")

    req.availabilityId.takeUnless {
      availabilityRepository.existsByIdAndSelected(id = req.availabilityId, selected = true)
    } ?: throw UnprocessableEntityException("Ya existe una sesión con esa disponibilidad")

    val subject =
      subjectsRepository.findByIdOrNull(req.subjectId)
        ?: throw EntityNotFoundException("La materia elegida no existe")
    val tutor =
      tutorsRepository.findByIdOrNull(authHelper.userId())
        ?: throw EntityNotFoundException("Este tutor no existe")

    // ::class.java needed to satisfy jdbc requirements (Class<Int>)
    val tutorAvailability =
      jdbcTemplate.queryForObject(
        """
        SELECT COUNT(*)
        FROM availability av
        INNER JOIN sessions s ON s.availability_id = av.id
        INNER JOIN session_assignment sa ON s.id = sa.session_id
        WHERE av.date = ?
        AND av.schedule_id = ?
        AND av.selected = ?
        AND sa.tutor_id = ?
          """,
        Int::class.java,
        availability.date,
        availability.scheduleId.id,
        true,
        tutor.id,
      )
    if (tutorAvailability != 0)
      throw IllegalArgumentException(
        "${tutor.fullname}, ya tienes una sesión programada a esa hora"
      )
    val session =
      sessionsRepository.save(
        SessionsEntity(
          expectedStudents = req.amountOfStudents,
          availableSlots = req.amountOfStudents,
          availabilityId = availability,
          endDatetime = availability.endDatetime as LocalDateTime,
        )
      )

    sessionAssignmentRepository.save(
      SessionAssignmentEntity(
        id =
          // Needed to create an instance of embedded id data class
          SessionAssignmentId(
            subjectId = subject.id as String,
            tutorId = tutor.id as String,
            sessionId = session.id as String,
          ),
        subjectId = subject,
        tutorId = tutor,
        sessionId = session,
      )
    )
    availability.selected = true
    availabilityRepository.save(availability)
    return ResponseEntity.status(HttpStatus.CREATED.value())
      .body(
        SuccessRes(
          statusCode = HttpStatus.CREATED.value(),
          content =
            "Sesión de ${subject.name} creada para el ${DateAndTimeHelper.formatDate(availability.date)} desde las ${availability.scheduleId.startTime} hasta las ${availability.scheduleId.endTime}",
        )
      )
  }

  // NOTE public function to find sessions in which student can enroll per subject
  fun getSessionsBySubjectId(
    subjectId: UUID
  ): ResponseEntity<SuccessRes<List<SessionsForInscriptionRes>>> {
    val subject =
      subjectsRepository.findByIdOrNull(subjectId.toString())
        ?: throw EntityNotFoundException("La materia ingresada no existe")
    val sessions = getListOfSessionsBySubjectId(subjectId.toString())
    if (sessions.isEmpty())
      throw EntityNotFoundException("No hay sesiones disponibles para ${subject.name}")
    return ResponseEntity.ok()
      .body(SuccessRes(statusCode = HttpStatus.OK.value(), content = sessions))
  }

  // NOTE this function finds session in which student can enroll per subject
  private fun getListOfSessionsBySubjectId(subjectId: String): List<SessionsForInscriptionRes> {
    val sessions =
      jdbcTemplate.query(
        """
        SELECT av.class_id as classId,
           tu.fullname as tutorName,
           tu.picture as tutorPicture,
           tu.score as tutorScore,
           sub.name as subjectName,
           sub.description as subjectDesc,
           av.date as sessionDate,
           sch.start_time as sessionStartTime,
           sch.end_time as sessionEndTime,
           s.id as sessionId,
           (SELECT COUNT(*) FROM inscriptions ins WHERE ins.session_id = s.id) as amountOfStudents,
           s.expected_students as sessionSlots
    FROM availability av
    INNER JOIN sessions s ON av.id = s.availability_id
    INNER JOIN session_assignment sa ON s.id = sa.session_id
    INNER JOIN tutors tu ON sa.tutor_id = tu.id
    INNER JOIN subjects sub ON sa.subject_id = sub.id
    INNER JOIN schedules sch ON av.schedule_id = sch.id
    LEFT JOIN inscriptions ins ON s.id = ins.session_id AND ins.student_id = ?
    WHERE ins.session_id IS NULL AND sub.id = ? AND TIMESTAMP(av.date, sch.start_time) > NOW()
      """,
        { rs, _ ->
          SessionsForInscriptionRes(
            tutorName = rs.getString("tutorName"),
            tutorScore = rs.getBigDecimal("tutorScore"),
            classId = rs.getString("classId"),
            sessionDate = DateAndTimeHelper.formatDate(rs.getDate("sessionDate").toLocalDate()),
            sessionStartTime =
              DateAndTimeHelper.formatTime(rs.getTime("sessionStartTime").toLocalTime()),
            sessionEndTime =
              DateAndTimeHelper.formatTime(rs.getTime("sessionEndTime").toLocalTime()),
            amountOfStudents = rs.getInt("amountOfStudents"),
            sessionSlots = rs.getInt("sessionSlots"),
            sessionId = rs.getString("sessionId"),
          )
        },
        authHelper.userId(),
        subjectId,
      )
    return sessions
  }
// NOTE this function retrieves all sessions of a specific tutor where a student can participate
  fun getListOfSessionsByTutorId(
    tutorId: UUID
  ): ResponseEntity<SuccessRes<List<SessionsForInscriptionRes>>> {
    tutorsRepository.findById(tutorId.toString()).orElseThrow {
      EntityNotFoundException("Este tutor no existe")
    }
    val tutorSessions =
      jdbcTemplate.query(
        """
        SELECT av.class_id as classId,
           tu.fullname as tutorName,
           tu.score as tutorScore,
           av.date as sessionDate,
           sch.start_time as sessionStartTime,
           sch.end_time as sessionEndTime,
           s.id as sessionId,
           (SELECT COUNT(*) FROM inscriptions ins WHERE ins.session_id = s.id) as amountOfStudents,
           s.expected_students as sessionSlots
    FROM availability av
    INNER JOIN sessions s ON av.id = s.availability_id
    INNER JOIN session_assignment sa ON s.id = sa.session_id
    INNER JOIN tutors tu ON sa.tutor_id = tu.id
    INNER JOIN subjects sub ON sa.subject_id = sub.id
    INNER JOIN schedules sch ON av.schedule_id = sch.id
    LEFT JOIN inscriptions ins ON s.id = ins.session_id AND ins.student_id = ?
    WHERE ins.session_id IS NULL AND tu.id = ? AND TIMESTAMP(av.date, sch.start_time) > NOW()
      """,
        { rs, _ ->
          SessionsForInscriptionRes(
            tutorName = rs.getString("tutorName"),
            tutorScore = rs.getBigDecimal("tutorScore"),
            classId = rs.getString("classId"),
            sessionDate = DateAndTimeHelper.formatDate(rs.getDate("sessionDate").toLocalDate()),
            sessionStartTime =
              DateAndTimeHelper.formatTime(rs.getTime("sessionStartTime").toLocalTime()),
            sessionEndTime =
              DateAndTimeHelper.formatTime(rs.getTime("sessionEndTime").toLocalTime()),
            amountOfStudents = rs.getInt("amountOfStudents"),
            sessionSlots = rs.getInt("sessionSlots"),
            sessionId = rs.getString("sessionId"),
          )
        },
        authHelper.userId(),
        tutorId.toString(),
      )
    return ResponseEntity.ok()
      .body(SuccessRes(statusCode = HttpStatus.OK.value(), content = tutorSessions))
  }

  @Transactional
  fun cancelSession(sessionId: String): ResponseEntity<SuccessRes<String>> {
    val session =
      sessionsRepository.findByIdOrNull(sessionId)
        ?: throw EntityNotFoundException("No se encontró la sesión solicitada")
    val scheduleStartTime = session.availabilityId.scheduleId.startTime
    val sessionDate = session.availabilityId.date
    val sessionStart = LocalDateTime.of(sessionDate, scheduleStartTime)
    val validDeleteDateTime = sessionStart.minusDays(1)
    val sessionAssigment =
      sessionAssignmentRepository.findBySessionId(session)
        ?: throw IllegalArgumentException("La sesión solicitada no ha sido asignada")
    val subjectName = sessionAssigment.subjectId.name
    val tutorId = sessionAssigment.tutorId.id
    if (tutorId != null && tutorId != authHelper.userId()) {
      throw EntityNotFoundException(
        "No se encontró la sesión solicitada entre su lista de sesiones pendientes"
      )
    }
    if (session.status != SessionStatus.ACTIVE)
      throw IllegalArgumentException("Esta sesión ya no está disponible")
    if (LocalDateTime.now().isAfter(validDeleteDateTime))
      throw IllegalArgumentException(
        "Las sesiones no pueden cancelarse dentro de las 24 horas previas a su inicio"
      )
    session.status = SessionStatus.CANCELLED
    val listOfStudentEmails = mutableListOf<String>()
    val inscriptions = inscriptionsRepository.findBySessionId(session)
    inscriptions
      .takeIf { it.isNotEmpty() }
      ?.let {
        it.forEach { inscription -> listOfStudentEmails.add(inscription.studentId.email) }
        inscriptionsRepository.deleteBySessionId(session)
        sendGridService.sendEmailAfterSessionCancel(
          listOfStudentEmails,
          subjectName,
          sessionStart,
          session.availabilityId.classId.id,
        )
      }
    return ResponseEntity.ok()
      .body(SuccessRes(statusCode = HttpStatus.OK.value(), content = "Se canceló la sesión"))
  }
}

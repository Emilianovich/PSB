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
import ptystudybuddy.psb.entities.SessionAssignmentEntity
import ptystudybuddy.psb.entities.SessionAssignmentId
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
  private val sessionFilter: SessionFilter,
  private val studentSessions: StudentSessionsRepository,
  private val studentSessionFilter: StudentSessionFilter,
  private val bucketService: BucketService,
) {
  // TODO Get One Service
  // REVIEW
  // TODO Evaluate if we should send formatted date and time to frontend
  fun filterSessions(req: SessionFiltersReq): ResponseEntity<SuccessRes<List<SessionSummaryRes>>> {
    val query = sessionFilter.getSessions(authHelper.userRole(), req, authHelper.userId())
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

  fun getSessionsBySubjectId(
    subjectId: UUID
  ): ResponseEntity<SuccessRes<List<SessionsForInscriptionRes>>> {
    val subject =
      subjectsRepository.findByIdOrNull(subjectId.toString())
        ?: throw EntityNotFoundException("La materia ingresada no existe")
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
        subject.id,
      )
    if (sessions.isEmpty())
      throw EntityNotFoundException("No hay sesiones disponibles para ${subject.name}")
    return ResponseEntity.ok()
      .body(SuccessRes(statusCode = HttpStatus.OK.value(), content = sessions))
  }
}

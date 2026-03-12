package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import kotlin.String
import org.hibernate.annotations.Immutable
import ptystudybuddy.psb.helpers.DateAndTimeHelper

@Entity
@Immutable
@Table(name = "v_sessions_summary")
class SessionSummaryEntity(
  @Id val id: Long,
  @Column val classId: String,
  @Column val sessionDate: LocalDate,
  // TODO Enum annotation validation
  @Column val sessionStatus: String,
  @Column val sessionStartTime: LocalTime,
  @Column val sessionEndTime: LocalTime,
  @Column val tutorName: String,
  @Column val tutorId: String,
  @Column var tutorPicture: String,
  @Column(precision = 3, scale = 2) val tutorScore: BigDecimal,
  @Column val subjectName: String,
  @Column val subjectId: String,
  @Column val subjectDesc: String,
  @Column val scheduleId: String,
  @Column val amountOfStudents: Int,
  @Column val sessionSlots: Int,
)

data class SessionSummaryRes(
  val classId: String,
  val sessionDate: String,
  // TODO Enum annotation validation
  val sessionStatus: String,
  val sessionStartTime: String,
  val sessionEndTime: String,
  val tutorName: String,
  val tutorId: String,
  var tutorPicture: String,
  val tutorScore: BigDecimal,
  val subjectName: String,
  val subjectId: String,
  val subjectDesc: String,
  val scheduleId: String,
  val amountOfStudents: Int,
  val sessionSlots: Int,
)

fun SessionSummaryEntity.toSessionSummaryRes(): SessionSummaryRes {
  return SessionSummaryRes(
    classId = classId,
    sessionDate = DateAndTimeHelper.formatDate(sessionDate),
    sessionStatus = sessionStatus,
    sessionStartTime = DateAndTimeHelper.formatTime(sessionStartTime),
    sessionEndTime = DateAndTimeHelper.formatTime(sessionEndTime),
    tutorName = tutorName,
    tutorId = tutorId,
    tutorPicture = tutorPicture,
    tutorScore = tutorScore,
    subjectName = subjectName,
    subjectId = subjectId,
    subjectDesc = subjectDesc,
    scheduleId = scheduleId,
    amountOfStudents = amountOfStudents,
    sessionSlots = sessionSlots,
  )
}

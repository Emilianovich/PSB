package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import org.hibernate.annotations.Immutable
import ptystudybuddy.psb.helpers.DateAndTimeHelper

@Entity
@Immutable
@Table(name = "v_students_sessions")
class StudentSessionsEntity(
  @Id val id: Long,
  @Column val tutorName: String,
  @Column val tutorId: String,
  @Column(precision = 3, scale = 2) val tutorScore: BigDecimal,
  @Column val tutorPicture: String,
  @Column val classId: String,
  @Column val subjectName: String,
  @Column val sessionDate: LocalDate,
  @Column val sessionStartTime: LocalTime,
  @Column val sessionEndTime: LocalTime,
  @Column val hasEvaluated: Boolean?,
  // TODO Enum annotation validation
  @Column val sessionStatus: String,
  // TODO Maybe change to UUID
  @Column val studentId: String,
  @Column val assisted: Boolean,
)

data class StudentSessionsRes(
  val classId: String,
  val subjectName: String,
  val tutorName: String,
  val tutorScore: BigDecimal,
  var tutorPicture: String,
  val sessionDate: String,
  val sessionStartTime: String,
  val sessionEndTime: String,
  val hasEvaluated: Boolean?,
)

fun StudentSessionsEntity.toStudentSessionsRes(): StudentSessionsRes {
  return StudentSessionsRes(
    classId = this.classId,
    subjectName = this.subjectName,
    tutorName = this.tutorName,
    tutorScore = this.tutorScore,
    tutorPicture = this.tutorPicture,
    sessionDate = DateAndTimeHelper.formatDate(this.sessionDate),
    sessionStartTime = DateAndTimeHelper.formatTime(sessionStartTime),
    sessionEndTime = DateAndTimeHelper.formatTime(sessionEndTime),
    hasEvaluated = this.hasEvaluated,
  )
}

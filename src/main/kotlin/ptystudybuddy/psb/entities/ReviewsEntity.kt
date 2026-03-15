package ptystudybuddy.psb.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import ptystudybuddy.psb.reviews.ReviewsDto
import java.time.LocalDateTime

@Entity
@Table(name = "reviews")
class ReviewsEntity(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null,
  @field:Size(min = 10, message = "El comentario debe ser mayor a 10 caracteres")
  @Column
  val comment: String?,
  @field:Min(1, message = "La calificación mínima es 1")
  @field:Max(5, message = "La calificación máxima es 5")
  @Column(nullable = false)
  val rating: Int,
  @Column(columnDefinition = "TIMESTAMP", name = "date_time") val dateTime: LocalDateTime? = null,
  @ManyToOne @JoinColumn(name = "student_id") val studentId: StudentsEntity,
  @ManyToOne @JoinColumn(name = "tutor_id") val tutorId: TutorsEntity,
  @ManyToOne @JoinColumn(name = "session_id") val sessionId: SessionsEntity,
)

fun ReviewsEntity.toDto(): ReviewsDto {

    return ReviewsDto(

        classroom = this.sessionId.availabilityId.classId.id,
        tutorName = this.tutorId.fullname,
        score = this.tutorId.score,
        schedule = "${this.sessionId.availabilityId.scheduleId.startTime} - ${this.sessionId.availabilityId.scheduleId.endTime}",
        subjectName = this.sessionId.sessionsAssignment
            .firstOrNull { it.sessionId == sessionId  }?.subjectId?.name,
        subjectDescription = this.sessionId.sessionsAssignment
            .firstOrNull { it.sessionId == sessionId  }?.subjectId?.description,
        studentsAmount = this.sessionId.expectedStudents - this.sessionId.availableSlots

    )


}

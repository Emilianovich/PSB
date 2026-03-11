package ptystudybuddy.psb.entities

import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.io.Serializable

@Embeddable
data class SessionAssignmentId(
  val subject_id: String,
  val tutor_id: String,
  val session_id: String,
) : Serializable

@Entity
@Table(name = "session_assignment")
class SessionAssignmentEntity(
  @EmbeddedId val id: SessionAssignmentId,
  @ManyToOne
  @MapsId("subject_id")
  @JoinColumn(name = "subject_id", nullable = false)
  val subject_id: SubjectsEntity,
  @ManyToOne
  @MapsId("tutor_id")
  @JoinColumn(name = "tutor_id", nullable = false)
  val tutor_id: TutorsEntity,
  @ManyToOne
  @MapsId("session_id")
  @JoinColumn(name = "session_id", nullable = false)
  val session_id: SessionsEntity,
)

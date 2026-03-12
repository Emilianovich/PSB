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
data class SessionAssignmentId(val subjectId: String, val tutorId: String, val sessionId: String) :
  Serializable

@Entity
@Table(name = "session_assignment")
class SessionAssignmentEntity(
  @EmbeddedId val id: SessionAssignmentId,
  @ManyToOne
  @MapsId("subjectId")
  @JoinColumn(name = "subject_id", nullable = false)
  val subjectId: SubjectsEntity,
  @ManyToOne
  @MapsId("tutorId")
  @JoinColumn(name = "tutor_id", nullable = false)
  val tutorId: TutorsEntity,
  @ManyToOne
  @MapsId("sessionId")
  @JoinColumn(name = "session_id", nullable = false)
  val sessionId: SessionsEntity,
)

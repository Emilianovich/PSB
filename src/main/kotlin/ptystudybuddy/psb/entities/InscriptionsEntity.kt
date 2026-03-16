package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDateTime

@Embeddable
data class InscriptionId(
  @Column(columnDefinition = "CHAR(36)") val studentId: String,
  @Column(columnDefinition = "CHAR(36)") val sessionId: String,
) : Serializable

@Entity
@Table(name = "inscriptions")
class InscriptionsEntity(
  @EmbeddedId val id: InscriptionId,
  @ManyToOne @MapsId("studentId") @JoinColumn(name = "student_id") val studentId: StudentsEntity,
  @ManyToOne @MapsId("sessionId") @JoinColumn(name = "session_id") val sessionId: SessionsEntity,
  @Column var assisted: Boolean? = null,
  @Column(name = "evaluation_status") var evaluationStatus: Boolean? = null,
  @Column(columnDefinition = "TIMESTAMP", name = "date_time") val dateTime: LocalDateTime? = null,
)

data class InscriptionRes(
  val studentName: String,
  val studentPicture: String,
  val studentEmail: String,
)

fun InscriptionsEntity.toInscriptionRes() =
  InscriptionRes(
    studentName = studentId.fullname,
    studentPicture = studentId.picture,
    studentEmail = studentId.email,
  )

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
    val student_id: String,
    val session_id: String,
) : Serializable

@Entity
@Table(name = "inscriptions")
class InscriptionsEntity(
    @EmbeddedId
    val id: InscriptionId,
    @ManyToOne
    @MapsId("student_id")
    @JoinColumn(name = "student_id")
    val student_id: StudentsEntity,
    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    val session_id: SessionsEntity,
    @Column
    var assisted: Boolean? = null,
    @Column
    var evaluation_status: Boolean? = null,
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    val date_time: LocalDateTime? = null,
)

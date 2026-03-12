package ptystudybuddy.psb.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "sessions")
class SessionsEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "CHAR(36)")
  val id: String? = null,
  @Column(nullable = false, name = "expected_students") val expectedStudents: Int,
  @Column(name = "available_slots") val availableSlots: Int,
  @Column(name = "attendance_marked") var attendanceMarked: Boolean? = null,
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  var status: SessionStatus = SessionStatus.ACTIVE,
  @ManyToOne
  @JoinColumn(name = "availability_id", nullable = false)
  val availabilityId: AvailabilityEntity,
  @Column(nullable = false, columnDefinition = "TIMESTAMP", name = "end_datetime")
  val endDatetime: LocalDateTime,
  @OneToMany(mappedBy = "sessionId")
  @JsonIgnore
  val reviews: MutableList<ReviewsEntity> = mutableListOf(),
  @OneToMany(mappedBy = "sessionId")
  @JsonIgnore
  val inscriptions: MutableList<InscriptionsEntity> = mutableListOf(),
  @OneToMany(mappedBy = "sessionId")
  @JsonIgnore
  val sessionsAssignment: MutableList<SessionAssignmentEntity> = mutableListOf(),
)

enum class SessionStatus {
  ACTIVE,
  CANCELLED,
  NOT_ACTIVE,
}

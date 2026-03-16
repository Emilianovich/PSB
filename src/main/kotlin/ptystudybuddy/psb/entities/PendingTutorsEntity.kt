package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "pending_tutors")
class PendingTutorsEntity(
  @Id @GeneratedValue(strategy = GenerationType.UUID) val id: String? = null,
  @Column(name = "social_id", nullable = false, unique = true) val socialId: String,
  @Column(nullable = false) val fullname: String,
  @Column(nullable = false) var picture: String,
  @Column(nullable = false) var cv: String,
  @Column(nullable = false, unique = true) var email: String,
  @Column(nullable = false) var password: String,
  @Column var approved: Boolean? = null,
  @Column(name = "blacklisted_at") val blacklistedAt: LocalDateTime? = null,
)

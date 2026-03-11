package ptystudybuddy.psb.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "tutors")
class TutorsEntity(
  @Id @GeneratedValue(strategy = GenerationType.UUID) val id: String? = null,
  @Column(name = "social_id", nullable = false) val socialId: String,
  @Column(nullable = false) val fullname: String,
  @Column(nullable = false) val cv: String,
  @Column(nullable = false, unique = true) val email: String,
  @Column(nullable = false) val password: String,
  @Column val picture: String,
  @Column(precision = 3, scale = 2) val score: BigDecimal,
  val role: String = "TUTOR",
  @OneToMany(mappedBy = "tutor_id")
  @JsonIgnore
  val reviews: MutableList<ReviewsEntity> = mutableListOf(),
  @OneToMany(mappedBy = "tutor_id")
  @JsonIgnore
  val sessions_assignments: MutableList<SessionAssignmentEntity> = mutableListOf(),
)

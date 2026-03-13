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
import kotlin.String
import ptystudybuddy.psb.tutors.TutorsDto

@Entity
@Table(name = "tutors")
class TutorsEntity(
  @Id @GeneratedValue(strategy = GenerationType.UUID) val id: String? = null,
  @Column(name = "social_id", nullable = false) val socialId: String,
  @Column(nullable = false) val fullname: String,
  @Column(nullable = false) val cv: String,
  @Column(nullable = false, unique = true) val email: String,
  @Column(nullable = false) val password: String,
  @Column var picture: String,
  @Column(precision = 3, scale = 2) val score: BigDecimal,
  val role: String = "TUTOR",
  @OneToMany(mappedBy = "tutorId")
  @JsonIgnore
  val reviews: MutableList<ReviewsEntity> = mutableListOf(),
  @OneToMany(mappedBy = "tutorId")
  @JsonIgnore
  val sessionsAssignment: MutableList<SessionAssignmentEntity> = mutableListOf(),
)

fun TutorsEntity.toTutorsDto(): TutorsDto {

  return TutorsDto(
    socialId = this.socialId,
    fullname = this.fullname,
    cv = this.cv,
    email = this.email,
    picture = this.picture,
    role = this.role,
  )
}

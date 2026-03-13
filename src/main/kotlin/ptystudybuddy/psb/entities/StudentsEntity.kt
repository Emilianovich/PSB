package ptystudybuddy.psb.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import ptystudybuddy.psb.students.StudentsResponseDto

@Entity
@Table(name = "students")
class StudentsEntity(
  @Id @GeneratedValue(strategy = GenerationType.UUID) val id: String? = null,
  @Column(nullable = false) val fullname: String,
  @Column(nullable = false, name = "social_id") val socialId: String,
  @Column(nullable = false, unique = true) var email: String,
  @Column(nullable = false) val password: String,
  @Column(nullable = false) var picture: String,
  val role: String = "STUDENT",
  @OneToMany(mappedBy = "studentId")
  @JsonIgnore
  val reviews: MutableList<ReviewsEntity> = mutableListOf(),
  @OneToMany(mappedBy = "studentId")
  @JsonIgnore
  val inscriptions: MutableList<InscriptionsEntity> = mutableListOf(),
)

fun StudentsEntity.toStudentsDto(): StudentsResponseDto {

  return StudentsResponseDto(
    fullname = this.fullname,
    socialId = this.socialId,
    email = this.email,
    picture = this.picture,
  )
}

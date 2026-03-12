package ptystudybuddy.psb.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "students")
class StudentsEntity(
  @Id @GeneratedValue(strategy = GenerationType.UUID) val id: String? = null,
  @Column(nullable = false) val fullname: String,
  @Column(nullable = false, name = "social_id") val socialId: String,
  @Column(nullable = false, unique = true) val email: String,
  @Column(nullable = false) val password: String,
  @Column(nullable = false) val picture: String,
  val role: String = "STUDENT",
  @OneToMany(mappedBy = "studentId")
  @JsonIgnore
  val reviews: MutableList<ReviewsEntity> = mutableListOf(),
  @OneToMany(mappedBy = "studentId")
  @JsonIgnore
  val inscriptions: MutableList<InscriptionsEntity> = mutableListOf(),
)

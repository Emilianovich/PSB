package ptystudybuddy.psb.entities

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
  @Column(nullable = false) val social_id: String,
  @Column(nullable = false, unique = true) val email: String,
  @Column(nullable = false) val password: String,
  @Column(nullable = false) val picture: String,
  val role: String = "STUDENT",
  @OneToMany(mappedBy = "student_id") val reviews: MutableList<ReviewsEntity> = mutableListOf(),
  @OneToMany(mappedBy = "student_id")
  val inscriptions: MutableList<InscriptionsEntity> = mutableListOf(),
)

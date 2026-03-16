package ptystudybuddy.psb.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import ptystudybuddy.psb.subjects.SubjectsDto

@Entity
@Table(name = "subjects")
class SubjectsEntity(
  @Id @GeneratedValue(strategy = GenerationType.UUID) val id: String? = null,
  @Column(nullable = false) val name: String,
  @Column(nullable = false) val description: String,
  @OneToMany(mappedBy = "subjectId")
  @JsonIgnore
  val sessionsAssignment: MutableList<SessionAssignmentEntity> = mutableListOf(),
)

fun SubjectsEntity.toDto(): SubjectsDto {

  return SubjectsDto(name = this.name, description = this.description)
}

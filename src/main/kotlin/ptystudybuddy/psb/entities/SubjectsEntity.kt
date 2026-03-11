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
@Table(name = "subjects")
class SubjectsEntity(
  @Id @GeneratedValue(strategy = GenerationType.UUID) val id: String? = null,
  @Column(nullable = false) val name: String,
  @Column(nullable = false) val description: String,
  @OneToMany(mappedBy = "subject_id")
  @JsonIgnore
  val sessions_assignment: MutableList<SessionAssignmentEntity> = mutableListOf(),
)

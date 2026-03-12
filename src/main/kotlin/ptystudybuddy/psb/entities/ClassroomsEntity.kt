package ptystudybuddy.psb.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "classrooms")
class ClassroomsEntity(
  @Id val id: String,
  @Column(nullable = false) val location: String,
  @OneToMany(mappedBy = "classId")
  @JsonIgnore
  val availability: MutableList<AvailabilityEntity> = mutableListOf(),
)

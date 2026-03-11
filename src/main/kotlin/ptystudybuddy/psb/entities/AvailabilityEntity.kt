package ptystudybuddy.psb.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "availability")
class AvailabilityEntity(
  @Id val id: String? = null,
  @ManyToOne @JoinColumn(name = "class_id", nullable = false) val class_id: ClassroomsEntity,
  @ManyToOne @JoinColumn(name = "schedule_id", nullable = false) val schedule_id: SchedulesEntity,
  @Column(nullable = false) val date: LocalDate,
  @Column val selected: Boolean? = null,
  @Column(columnDefinition = "TIMESTAMP") val end_datetime: LocalDateTime? = null,
  @OneToMany(mappedBy = "availability_id")
  @JsonIgnore
  val sessions: MutableList<SessionsEntity> = mutableListOf(),
)

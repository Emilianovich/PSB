package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalTime

@Entity
@Table(name = "schedules")
class SchedulesEntity(
  @Id val id: String,
  @Column(nullable = false, columnDefinition = "TIME") val start_time: LocalTime,
  @Column(nullable = false, columnDefinition = "TIME") val end_time: LocalTime,
  @OneToMany(mappedBy = "schedule_id")
  val availability: MutableList<AvailabilityEntity> = mutableListOf(),
)

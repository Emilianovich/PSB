package ptystudybuddy.psb.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalTime
import ptystudybuddy.psb.helpers.DateAndTimeHelper

@Entity
@Table(name = "schedules")
class SchedulesEntity(
  @Id @Column(columnDefinition = "CHAR(4)") val id: String,
  @Column(nullable = false, columnDefinition = "TIME", name = "start_time")
  val startTime: LocalTime,
  @Column(nullable = false, columnDefinition = "TIME", name = "end_time") val endTime: LocalTime,
  @OneToMany(mappedBy = "scheduleId")
  @JsonIgnore
  val availability: MutableList<AvailabilityEntity> = mutableListOf(),
)

data class ScheduleRes(val id: String, val startTime: String, val endTime: String)

fun SchedulesEntity.toScheduleRes(): ScheduleRes {
  return ScheduleRes(
    id = id,
    startTime = DateAndTimeHelper.formatTime(startTime),
    endTime = DateAndTimeHelper.formatTime(endTime),
  )
}

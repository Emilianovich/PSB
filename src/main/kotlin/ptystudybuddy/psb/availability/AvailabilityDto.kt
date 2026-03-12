package ptystudybuddy.psb.availability

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import ptystudybuddy.psb.entities.AvailabilityEntity
import ptystudybuddy.psb.helpers.DateAndTimeHelper

data class AvailabilityDto(
  @field:NotBlank val class_id: String,
  @field:NotBlank val schedule_id: String,
  @field:NotNull val date: LocalDate,
)

data class AvailabilityRes(
  val availabilityId: String,
  val classId: String,
  val scheduleId: String,
  val startTime: String,
  val endTime: String,
  val date: LocalDate,
)

fun AvailabilityEntity.toResponseDto(): AvailabilityRes {
  return AvailabilityRes(
    availabilityId = this.id as String,
    classId = this.classId.id,
    scheduleId = this.scheduleId.id,
    startTime = DateAndTimeHelper.formatTime(this.scheduleId.startTime),
    endTime = DateAndTimeHelper.formatTime(this.scheduleId.endTime),
    date = this.date,
  )
}

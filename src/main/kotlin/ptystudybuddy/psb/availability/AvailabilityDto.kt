package ptystudybuddy.psb.availability

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import ptystudybuddy.psb.entities.AvailabilityEntity

data class AvailabilityDto(
  @field:NotBlank val class_id: String,
  @field:NotBlank val schedule_id: String,
  @field:NotNull val date: LocalDate,
)

fun AvailabilityEntity.toResponseDto(): AvailabilityDto {
  return AvailabilityDto(
    class_id = this.class_id.id,
    schedule_id = this.schedule_id.id,
    date = this.date,
  )
}

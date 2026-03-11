package ptystudybuddy.psb.reviews

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class CreateReviewDto(
  val comment: String? = null,
  @field:NotNull val rating: Int,
  @field:NotBlank val session_id: String,
)

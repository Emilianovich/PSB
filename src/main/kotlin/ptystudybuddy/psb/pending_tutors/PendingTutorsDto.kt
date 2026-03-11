package ptystudybuddy.psb.pending_tutors

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PendingTutorsApprovalDto(
  @field:NotBlank val candidate: String,
  @field:NotNull val approved: Boolean,
)

data class PendingTutorsResponseDto(
  val social_id: String,
  val fullName: String,
  val picture: String,
  val cv: String,
  val email: String,
)

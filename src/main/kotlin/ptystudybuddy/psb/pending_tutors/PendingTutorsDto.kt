package ptystudybuddy.psb.pending_tutors

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PendingTutorsApprovalDto(
  @field:NotBlank val candidate: String,
  @field:NotNull val approved: Boolean,
)

data class PendingTutorsResponseDto(
  @field:NotBlank val id: String,
  @field:NotBlank val social_id: String,
  @field:NotBlank val fullName: String,
  @field:NotBlank val picture: String,
  @field:NotBlank val cv: String,
  @field:NotBlank val email: String,
)

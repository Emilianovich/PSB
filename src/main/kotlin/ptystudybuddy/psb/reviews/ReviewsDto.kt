package ptystudybuddy.psb.reviews

import java.math.BigDecimal

data class ReviewsDto(
  val classroom: String,
  val tutorName: String,
  val score: BigDecimal,
  val schedule: String,
  val subjectName: String?,
  val subjectDescription: String?,
  val studentsAmount: Int,
)

data class CreateReviewRequest(val comment: String? = null, val rating: Int, val sessionId: String)

package ptystudybuddy.psb.presentation

import java.time.Instant

data class SuccessRes<T>(
  val statusCode: Int,
  val content: T,
  val timestamp: Instant = Instant.now(),
) {
  val success: Boolean = true
}

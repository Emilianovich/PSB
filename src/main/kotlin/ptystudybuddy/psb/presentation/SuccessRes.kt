package ptystudybuddy.psb.presentation

import java.time.Instant

data class SuccessRes<T>(
    val success: Boolean = true,
    val statusCode: Int,
    val errors: T,
    val timestamp: Instant = Instant.now(),
)

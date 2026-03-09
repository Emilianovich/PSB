package ptystudybuddy.psb.presentation

import java.time.Instant

data class ExceptionRes<T>(
    val success: Boolean = false,
    val statusCode: Int,
    val errors: T,
    val timestamp: Instant = Instant.now(),
)

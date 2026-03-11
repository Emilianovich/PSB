package ptystudybuddy.psb.helpers

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateAndTimeHelper {
  private val dateFormatter =
    DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale.of("es"))
  private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

  fun formatDate(date: LocalDate): String {
    return date.format(dateFormatter)
  }

  fun formatTime(time: LocalTime): String {
    return time.format(timeFormatter)
  }

  fun formatDatetime(date: LocalDateTime): String {
    return date.format(dateFormatter)
  }
}

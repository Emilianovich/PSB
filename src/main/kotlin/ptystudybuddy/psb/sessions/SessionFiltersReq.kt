package ptystudybuddy.psb.sessions

import java.time.LocalDate

data class SessionFiltersReq(
  val subjectId: String? = null,
  val date: LocalDate? = null,
  // TODO Add Enum Class validation
  val status: String? = null,
  val tutorId: String? = null,
)

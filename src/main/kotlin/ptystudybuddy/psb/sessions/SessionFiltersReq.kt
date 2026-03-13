package ptystudybuddy.psb.sessions

import java.time.LocalDate
import ptystudybuddy.psb.annotations.sessionStatus.SessionStatusValidator

data class SessionFiltersReq(
  val subjectId: String? = null,
  val date: LocalDate? = null,
  @field:SessionStatusValidator val status: String? = null,
  val tutorId: String? = null,
)

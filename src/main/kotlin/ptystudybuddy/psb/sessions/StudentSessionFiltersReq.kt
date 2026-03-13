package ptystudybuddy.psb.sessions

import ptystudybuddy.psb.annotations.sessionStatus.SessionStatusValidator

data class StudentSessionFiltersReq(
  @field:SessionStatusValidator val status: String? = null,
  val assisted: Boolean? = null,
)

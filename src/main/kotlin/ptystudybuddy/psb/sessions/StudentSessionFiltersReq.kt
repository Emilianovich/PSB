package ptystudybuddy.psb.sessions

data class StudentSessionFiltersReq(
  // TODO Maybe add Enum Class validation
  val status: String? = null,
  val assisted: Boolean? = null,
)

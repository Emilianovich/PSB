package ptystudybuddy.psb.inscriptions

data class MarkAttendanceReq(
  // NOTE Validation is quite tricky because technically students may not show up
  val studentId: String
)

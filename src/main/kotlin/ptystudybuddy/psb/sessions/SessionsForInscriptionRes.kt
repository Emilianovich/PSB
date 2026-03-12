package ptystudybuddy.psb.sessions

import java.math.BigDecimal

data class SessionsForInscriptionRes(
  val tutorName: String,
  val tutorScore: BigDecimal,
  val classId: String,
  val sessionDate: String,
  val sessionStartTime: String,
  val sessionEndTime: String,
  val amountOfStudents: Int,
  val sessionSlots: Int,
  val sessionId: String,
)

package ptystudybuddy.psb.sessions

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class CreateSession(
  @field:Min(5, message = "La cantidad mínima de estudiantes mínima es 5")
  @field:Max(25, message = "La cantidad máxima de estudiantes es 25")
  val amountOfStudents: Int,
  val subjectId: String,
  val availabilityId: String,
)

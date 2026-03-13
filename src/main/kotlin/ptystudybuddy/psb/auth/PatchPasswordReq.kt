package ptystudybuddy.psb.auth

import jakarta.validation.constraints.Pattern

data class PatchPasswordReq(
  @field:Pattern(
    regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$¡*]).{9,}$",
    message =
      "Su contraseña debe tener más de 8 caracteres, letra(s), números y un carácter especial",
  )
  val password: String,
  @field:Pattern(
    regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$¡*]).{9,}$",
    message =
      "Su contraseña debe tener más de 8 caracteres, letra(s), números y un carácter especial",
  )
  val confirmPassword: String,
)

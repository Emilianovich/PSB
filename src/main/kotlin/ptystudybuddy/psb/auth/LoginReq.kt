package ptystudybuddy.psb.auth

import jakarta.validation.constraints.NotBlank

data class LoginReq(
  @field:NotBlank(message = "El campo del correo no puede quedar estar vacío") val email: String,
  @field:NotBlank(message = "El campo de la contraseña no puede quedar estar vacío")
  val password: String,
)

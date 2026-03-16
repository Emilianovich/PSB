package ptystudybuddy.psb.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile
import ptystudybuddy.psb.annotations.email.EmailExists
import ptystudybuddy.psb.annotations.files.image.ImageValidator
import ptystudybuddy.psb.annotations.socialId.SocialIdExists

data class StudentRegisterReq(
  @field:NotBlank(message = "Su nombre completo es requerido")
  @field:Size(max = 35, message = "Su nombre completo no puede exceder los 35 caracteres")
  val fullName: String,
  @field:Pattern(
    regexp = "^(?:[1-9]|1[0-3]|E|N|PE)-[0-9]{1,4}-[0-9]{1,6}$",
    message = "Su cédula no es válida para panameños",
  )
  @field:SocialIdExists
  val socialId: String,
  @field:Email(
    regexp = "^[a-zA-Z0-9._%+\\-]+@(?!ptystudybuddy\\.dev)[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$",
    message = "Ingrese un correo electrónico válido",
  )
  @field:EmailExists
  val email: String,
  @field:Pattern(
    regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$¡*]).{9,}$",
    message =
      "Su contraseña debe tener más de 8 caracteres, letra(s), números y un carácter especial",
  )
  val password: String,
  @field:ImageValidator val picture: MultipartFile,
)

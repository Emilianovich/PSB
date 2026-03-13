package ptystudybuddy.psb.students

import jakarta.validation.constraints.Email
import org.springframework.web.multipart.MultipartFile
import ptystudybuddy.psb.annotations.files.image.ImageValidator

data class StudentsResponseDto(
  val fullname: String,
  val socialId: String,
  var email: String,
  var picture: String,
)

data class StudentUpdateDto(
  @field:ImageValidator val picture: MultipartFile?,
  @field:Email(message = "Formato de correo erróneo") val email: String?,
)

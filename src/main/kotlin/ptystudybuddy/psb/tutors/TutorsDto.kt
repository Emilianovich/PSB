package ptystudybuddy.psb.tutors

import org.springframework.web.multipart.MultipartFile
import ptystudybuddy.psb.annotations.files.image.ImageValidator

data class TutorsDto(
  val socialId: String,
  val fullname: String,
  var cv: String,
  val email: String,
  var picture: String,
  val role: String,
)

data class TutorsUpdateDto(@field:ImageValidator var picture: MultipartFile)

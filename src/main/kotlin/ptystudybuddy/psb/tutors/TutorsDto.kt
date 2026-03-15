package ptystudybuddy.psb.tutors

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.web.multipart.MultipartFile
import ptystudybuddy.psb.annotations.files.image.ImageValidator

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TutorsDto(
  val socialId: String,
  val fullname: String,
  var cv: String,
  val email: String,
  var picture: String,
  val role: String,
  var sessionsAmount: Long? = null,
)

data class TutorsUpdateDto(@field:ImageValidator var picture: MultipartFile)

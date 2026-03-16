package ptystudybuddy.psb.students

import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ptystudybuddy.psb.auth.StudentRegisterReq

@RestController
@RequestMapping("/students")
@PreAuthorize("hasRole('STUDENT')")
class StudentsController(private val studentsService: StudentsService) {

  @PostMapping("/register", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
  fun studentRegister(@Valid @ModelAttribute req: StudentRegisterReq) =
    studentsService.registerStudent(req)

  @GetMapping("/profile") fun getProfile() = studentsService.getProfile()

  @PatchMapping("/profile")
  fun updateCredentials(@Valid @ModelAttribute studentsUpdateDto: StudentUpdateDto) =
    studentsService.updateCredentials(studentsUpdateDto)
}

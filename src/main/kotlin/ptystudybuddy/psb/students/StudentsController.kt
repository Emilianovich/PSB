package ptystudybuddy.psb.students

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/students")
@PreAuthorize("hasRole('STUDENT')")
class StudentsController(val studentsService: StudentsService) {

  @GetMapping("/profile") fun getProfile() = studentsService.getProfile()

  @PatchMapping("/profile")
  fun updateCredentials(@Valid @ModelAttribute studentsUpdateDto: StudentUpdateDto) =
    studentsService.updateCredentials(studentsUpdateDto)
}

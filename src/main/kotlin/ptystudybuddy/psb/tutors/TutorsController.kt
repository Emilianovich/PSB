package ptystudybuddy.psb.tutors

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tutors")
class TutorsController(private val tutorsService: TutorsService) {

  @PreAuthorize("hasRole('TUTOR')")
  @GetMapping("/profile")
  fun getProfile() = tutorsService.getProfile()

  @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
  @GetMapping
  fun getAllTutors(
    @RequestParam("fullname", required = false) fullName: String?,
    @RequestParam("orderBy", required = false) orderBy: String = "ASC",
  ) = tutorsService.getAllTutors(fullName, orderBy)

  @PreAuthorize("hasRole('TUTOR')")
  @PatchMapping("/profile")
  fun updateCredentials(@Valid @ModelAttribute tutorsUpdateDto: TutorsUpdateDto) =
    tutorsService.updateCredentials(tutorsUpdateDto)
}

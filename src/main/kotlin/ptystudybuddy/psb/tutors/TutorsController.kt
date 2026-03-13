package ptystudybuddy.psb.tutors

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tutors")
@PreAuthorize("hasRole('TUTOR')")
class TutorsController(val tutorsService: TutorsService) {

  @GetMapping("/profile") fun getProfile() = tutorsService.getProfile()

  @GetMapping
  fun getAllTutors(
    @RequestParam("fullname", required = false) fullName: String?,
    @RequestParam("orderBy", required = false) orderBy: String = "ASC",
  ) = tutorsService.getAllTutors(fullName, orderBy)

  @PatchMapping("/profile")
  fun updateCredentials(@ModelAttribute tutorsUpdateDto: TutorsUpdateDto) =
    tutorsService.updateCredentials(tutorsUpdateDto)
}

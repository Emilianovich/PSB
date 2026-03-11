package ptystudybuddy.psb.pending_tutors

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ptystudybuddy.psb.presentation.SuccessRes

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/pending-tutors")
class PendingTutorsController(private val pendingTutorsService: PendingTutorsService) {

  @GetMapping("") fun getAllPendingTutors() = pendingTutorsService.getAllPendingTutors()

  @GetMapping("/{candidateId}")
  fun getOnePendingTutor(@PathVariable candidateId: String) =
    pendingTutorsService.getOnePendingTutor(candidateId)

  @PatchMapping("/approval")
  fun manageTutorApproval(
    @Valid @RequestBody pendingTutorsData: PendingTutorsApprovalDto
  ): ResponseEntity<SuccessRes<String>> {

    return pendingTutorsService.manageTutorApproval(pendingTutorsData)
  }
}

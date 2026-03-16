package ptystudybuddy.psb.inscriptions

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/inscriptions")
class InscriptionController(private val inscriptionService: InscriptionService) {
  @GetMapping("/{sessionId}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TUTOR')")
  fun getAll(@PathVariable sessionId: String) =
    inscriptionService.getAllInscriptionsPerSession(sessionId)

  @PostMapping
  @PreAuthorize("hasRole('STUDENT')")
  fun create(@Valid @RequestBody req: CreateInscriptionReq) = inscriptionService.create(req)

  @PatchMapping(path = ["{sessionId}"], consumes = ["application/json"])
  @PreAuthorize("hasRole('TUTOR')")
  fun markAttendance(
    @PathVariable sessionId: String,
    @RequestBody req: MutableList<MarkAttendanceReq>,
  ) = inscriptionService.markAttendance(sessionId, req)

  @DeleteMapping("/{sessionId}")
  @PreAuthorize("hasRole('STUDENT')")
  fun unsubscribe(@PathVariable sessionId: String) = inscriptionService.unsubscribe(sessionId)
}

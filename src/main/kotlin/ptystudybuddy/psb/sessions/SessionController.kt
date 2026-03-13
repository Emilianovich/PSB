package ptystudybuddy.psb.sessions

import jakarta.validation.Valid
import java.util.UUID
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sessions")
class SessionController(private val sessionService: SessionService) {
  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/{sessionId}")
  fun getSession(@PathVariable sessionId: String) = sessionService.getOneSession(sessionId)

  @PreAuthorize("hasRole('TUTOR') or hasRole('ADMIN')")
  @GetMapping
  fun getAll(@Valid @ModelAttribute req: SessionFiltersReq) = sessionService.filterSessions(req)

  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/available/{subjectId}")
  fun getBySubjectId(@PathVariable subjectId: UUID) =
    sessionService.getSessionsBySubjectId(subjectId)

  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/student")
  fun getStudentSessions(@Valid @ModelAttribute req: StudentSessionFiltersReq) =
    sessionService.handleStudentFilter(req)

  @PreAuthorize("hasRole('TUTOR')")
  @PostMapping
  fun create(@Valid @RequestBody req: CreateSession) = sessionService.createSession(req)
}

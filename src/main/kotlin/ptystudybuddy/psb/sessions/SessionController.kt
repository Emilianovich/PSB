package ptystudybuddy.psb.sessions

import jakarta.validation.Valid
import java.time.LocalDate
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ptystudybuddy.psb.entities.SessionSummaryEntity
import ptystudybuddy.psb.entities.SessionSummaryRes
import ptystudybuddy.psb.entities.StudentSessionsRes
import ptystudybuddy.psb.presentation.SuccessRes

@RestController
@RequestMapping("/sessions")
class SessionController(private val sessionService: SessionService) {
  @PreAuthorize("hasRole('TUTOR') or hasRole('ADMIN')")
  @GetMapping
  fun getAll(
    @RequestParam subjectId: String?,
    @RequestParam date: LocalDate?,
    // TODO Maybe change type to ENUM Class
    @RequestParam status: String?,
    @RequestParam tutorId: String?,
  ): ResponseEntity<SuccessRes<List<SessionSummaryRes>>> =
    sessionService.filterSessions(
      SessionFiltersReq(subjectId = subjectId, date = date, status = status, tutorId = tutorId)
    )

  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/available/{subjectId}")
  fun getBySubjectId(@PathVariable subjectId: UUID) =
    sessionService.getSessionsBySubjectId(subjectId)

  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/student")
  fun getStudentSessions(
    @RequestParam status: String?,
    @RequestParam assisted: Boolean?,
  ): ResponseEntity<SuccessRes<List<StudentSessionsRes>>> =
    sessionService.handleStudentFilter(
      StudentSessionFiltersReq(status = status, assisted = assisted)
    )

  @PreAuthorize("hasRole('TUTOR')")
  @PostMapping
  fun create(@Valid @RequestBody req: CreateSession) = sessionService.createSession(req)
}

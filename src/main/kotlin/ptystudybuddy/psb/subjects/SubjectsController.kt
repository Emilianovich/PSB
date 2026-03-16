package ptystudybuddy.psb.subjects

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("hasRole('TUTOR') or hasRole('STUDENT')")
@RequestMapping("/subjects")
class SubjectsController(private val subjectsService: SubjectsService) {

  @GetMapping fun getAllSubjects() = subjectsService.getAllSubjects()
}

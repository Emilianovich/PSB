package ptystudybuddy.psb.schedules

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/schedules")
class ScheduleController(private val scheduleService: ScheduleService) {
  @PreAuthorize("hasRole('ADMIN') or hasRole('TUTOR')")
  @GetMapping
  fun getAllSchedules() = scheduleService.getAll()
}

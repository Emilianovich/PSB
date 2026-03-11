package ptystudybuddy.psb.classrooms

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/classrooms")
class ClassroomsController(val classroomsService: ClassroomsService) {
  @GetMapping fun getAllClassrooms() = this.classroomsService.getAllClassrooms()

  @PostMapping
  fun createClassroom(@Valid @RequestBody classroomData: MutableList<ClassroomDto>) =
    this.classroomsService.createClassroom(classroomData)
}

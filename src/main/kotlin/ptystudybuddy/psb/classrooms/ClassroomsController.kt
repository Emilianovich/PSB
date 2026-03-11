package ptystudybuddy.psb.classrooms

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/prueba")
class ClassroomsController(val classroomsService: ClassroomsService) {
    @GetMapping
    fun getAllClassrooms(): Any = this.classroomsService.getAllClassrooms()
}

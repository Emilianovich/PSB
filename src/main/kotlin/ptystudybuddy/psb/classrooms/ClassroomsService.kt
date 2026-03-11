package ptystudybuddy.psb.classrooms

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.ClassroomsEntity
import ptystudybuddy.psb.repositories.ClassroomsRepository

@Service
class ClassroomsService(val classroomsRepository: ClassroomsRepository) {
    fun getAllClassrooms(): MutableList<ClassroomsEntity> {
        val classrooms =
            this.classroomsRepository.findAll().takeIf { it.isNotEmpty() }
                ?: throw EntityNotFoundException("Classrooms Not Found")

        return classrooms
    }
}

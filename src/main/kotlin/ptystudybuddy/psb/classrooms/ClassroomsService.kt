package ptystudybuddy.psb.classrooms

import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.ClassroomsEntity
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.ClassroomsRepository

@Service
class ClassroomsService(val classroomsRepository: ClassroomsRepository) {
  fun getAllClassrooms(): ResponseEntity<SuccessRes<MutableList<ClassroomsEntity>>> {
    val classrooms =
      this.classroomsRepository.findAll().takeIf { it.isNotEmpty() }
        ?: throw EntityNotFoundException("No se encontraron salones")

    return ResponseEntity.ok(SuccessRes(statusCode = HttpStatus.OK.value(), content = classrooms))
  }

  fun createClassroom(
    classroomData: MutableList<CreateClassroomDto>
  ): ResponseEntity<SuccessRes<String>> {

    this.classroomsRepository
      .findByIdIn(classroomData.map { it.id })
      .takeIf { it.isNotEmpty() }
      ?.let {
        throw DataIntegrityViolationException("Ya existen los salones ${it.map { c -> c.id }}")
      }

    val classrooms = classroomData.map { ClassroomsEntity(it.id, it.location) }

    this.classroomsRepository.saveAll(classrooms)

    return ResponseEntity.ok(
      SuccessRes(
        statusCode = HttpStatus.CREATED.value(),
        content = if (classroomData.size > 1) "Salones creados" else "Salón creado",
      )
    )
  }
}

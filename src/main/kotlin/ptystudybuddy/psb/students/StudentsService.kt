package ptystudybuddy.psb.students

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.bucket.BucketService
import ptystudybuddy.psb.entities.toStudentsDto
import ptystudybuddy.psb.helpers.AuthHelper
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.StudentsRepository

@Service
class StudentsService(
  val studentsRepository: StudentsRepository,
  val authHelper: AuthHelper,
  val bucketService: BucketService,
) {

  fun getProfile(): ResponseEntity<SuccessRes<StudentsResponseDto>> {
    val userId = authHelper.userId()

    val student =
      studentsRepository
        .findById(userId)
        .orElseThrow { EntityNotFoundException("Estudiante no encontrado $userId") }
        .toStudentsDto()

    student.apply { picture = bucketService.getSignedUrl(picture) }

    return ResponseEntity.ok().body(SuccessRes(HttpStatus.OK.value(), student))
  }

  @Transactional
  fun updateCredentials(studentUpdateDto: StudentUpdateDto): ResponseEntity<SuccessRes<String>> {

    val userId = authHelper.userId()

    val student =
      studentsRepository.findById(userId).orElseThrow {
        EntityNotFoundException("Estudiante no encontrado $userId")
      }

    studentUpdateDto.picture?.let { student.picture = bucketService.update(it, student.picture) }

    studentUpdateDto.email?.let {
      studentsRepository.findByEmail(it)?.let { student ->
        if (student.id != userId)
          throw DataIntegrityViolationException("Ya existe un usuario con este correo")
      }

      student.email = it
    }

    return ResponseEntity.ok()
      .body(SuccessRes(HttpStatus.OK.value(), "Se han actualizado sus credenciales"))
  }
}

package ptystudybuddy.psb.subjects

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.toDto
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.SubjectsRepository

@Service
class SubjectsService(private val subjectsRepository: SubjectsRepository) {

  fun getAllSubjects(): ResponseEntity<SuccessRes<List<SubjectsDto>>> {

    val subjects = subjectsRepository.findAll().map { subject -> subject.toDto() }

    return ResponseEntity.ok(SuccessRes(HttpStatus.OK.value(), subjects))
  }
}

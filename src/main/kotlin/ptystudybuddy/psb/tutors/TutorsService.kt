package ptystudybuddy.psb.tutors

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.bucket.BucketService
import ptystudybuddy.psb.entities.toTutorsDto
import ptystudybuddy.psb.helpers.AuthHelper
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.TutorsRepository

@Service
class TutorsService(
  val tutorsRepository: TutorsRepository,
  val bucketService: BucketService,
  val authHelper: AuthHelper,
) {
  fun getAllTutors(
    fullname: String?,
    orderBy: String,
  ): ResponseEntity<SuccessRes<List<TutorsDto>>> {

    val tutors =
      tutorsRepository.findAll(TutorsSpecification.fullNameFilter(fullname, orderBy)).takeIf {
        it.isNotEmpty()
      } ?: throw EntityNotFoundException("No se encontraron tutores")

    val tutorsDtos =
      tutors.map { tutor ->
        val sessionsConInscripciones =
          tutor.sessionsAssignment.count { sa -> sa.sessionId.inscriptions.isNotEmpty() }

        tutor.toTutorsDto().apply {
          picture = bucketService.getSignedUrl(picture)
          cv = bucketService.getSignedUrl(cv)
          sessionsAmount = sessionsConInscripciones.toLong()
        }
      }

    return ResponseEntity.ok().body(SuccessRes(HttpStatus.OK.value(), tutorsDtos))
  }

  fun getProfile(): ResponseEntity<SuccessRes<TutorsDto>> {
    val userId = authHelper.userId()

    val tutor =
      tutorsRepository
        .findById(userId)
        .orElseThrow { EntityNotFoundException("Tutor no encontrado $userId") }
        .toTutorsDto()

    tutor.apply {
      this.picture = bucketService.getSignedUrl(picture)
      this.cv = bucketService.getSignedUrl(cv)
    }

    return ResponseEntity.ok().body(SuccessRes(HttpStatus.OK.value(), tutor))
  }

  @Transactional
  fun updateCredentials(tutorsUpdateDto: TutorsUpdateDto): ResponseEntity<SuccessRes<String>> {

    val tutorId = authHelper.userId()

    val tutor =
      tutorsRepository.findById(tutorId).orElseThrow {
        EntityNotFoundException("Tutor $tutorId no encontrado")
      }

    tutor.apply { this.picture = bucketService.update(tutorsUpdateDto.picture, this.picture) }

    return ResponseEntity.ok().body(SuccessRes(HttpStatus.OK.value(), "Foto de perfil actualizada"))
  }
}

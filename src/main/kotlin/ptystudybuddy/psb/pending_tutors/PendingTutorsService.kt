package ptystudybuddy.psb.pending_tutors

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import java.math.BigDecimal
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.bucket.BucketService
import ptystudybuddy.psb.email.SendGridService
import ptystudybuddy.psb.entities.TutorsEntity
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.PendingTutorsRepository
import ptystudybuddy.psb.repositories.TutorsRepository

@Service
class PendingTutorsService(
  val pendingTutorsRepository: PendingTutorsRepository,
  val tutorRepository: TutorsRepository,
  val emailService: SendGridService,
  val bucketService: BucketService,
) {

  fun getAllPendingTutors(): ResponseEntity<SuccessRes<List<PendingTutorsResponseDto>>> {

    val pendingTutors =
      pendingTutorsRepository.findAllByApprovedIsNull().takeIf { it.isNotEmpty() }
        ?: throw EntityNotFoundException("No hay tutores pendientes")

    val pendingTutorsResponse =
      pendingTutors.map {
        PendingTutorsResponseDto(
          id = it.id as String,
          social_id = it.socialId,
          fullName = it.fullname,
          email = it.email,
          picture = bucketService.getSignedUrl(it.picture),
          cv = bucketService.getSignedUrl(it.cv),
        )
      }

    return ResponseEntity.ok(SuccessRes(HttpStatus.OK.value(), pendingTutorsResponse))
  }

  fun getOnePendingTutor(
    candidateId: String
  ): ResponseEntity<SuccessRes<PendingTutorsResponseDto>> {

    val pendingTutors =
      pendingTutorsRepository.findById(candidateId).orElseThrow {
        EntityNotFoundException("No existe este tutor")
      }

    val pendingTutorResponse =
      PendingTutorsResponseDto(
        id = pendingTutors.id as String,
        social_id = pendingTutors.socialId,
        fullName = pendingTutors.fullname,
        email = pendingTutors.email,
        picture = bucketService.getSignedUrl(pendingTutors.picture),
        cv = bucketService.getSignedUrl(pendingTutors.cv),
      )
    return ResponseEntity.ok(SuccessRes(HttpStatus.OK.value(), pendingTutorResponse))
  }

  @Transactional
  fun manageTutorApproval(
    pendingTutorsData: PendingTutorsApprovalDto
  ): ResponseEntity<SuccessRes<String>> {

    val tutor =
      pendingTutorsRepository.findById(pendingTutorsData.candidate).orElseThrow {
        EntityNotFoundException("No se encontró al postulante")
      }

    tutor.takeIf { it.approved == null }
      ?: throw DataIntegrityViolationException(
        "El postulante se encuentra en blacklist, intentar despues de ${tutor.blacklistedAt}"
      )

    if (pendingTutorsData.approved) {

      val newTutor =
        TutorsEntity(
          socialId = tutor.socialId,
          fullname = tutor.fullname,
          cv = tutor.cv,
          email = tutor.email,
          password = tutor.password,
          picture = tutor.picture,
          score = BigDecimal("0.00"),
        )

      tutorRepository.save(newTutor)
      pendingTutorsRepository.delete(tutor)

      emailService.sendEmailAfterDecision(
        fullName = tutor.fullname,
        tutorEmail = tutor.email,
        status = true,
      )

      return ResponseEntity.ok(SuccessRes(HttpStatus.OK.value(), "Se ha aprobado al postulante"))
    } else {

      tutor.approved = false
    }

    emailService.sendEmailAfterDecision(
      fullName = tutor.fullname,
      tutorEmail = tutor.email,
      status = false,
    )

    return ResponseEntity.ok(SuccessRes(HttpStatus.OK.value(), "Se ha desaprobado al postulante"))
  }
}

package ptystudybuddy.psb.reviews

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.ReviewsEntity
import ptystudybuddy.psb.entities.TutorsEntity
import ptystudybuddy.psb.entities.toDto
import ptystudybuddy.psb.helpers.AuthHelper
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.ReviewsRepository
import ptystudybuddy.psb.repositories.SessionAssignmentRepository
import ptystudybuddy.psb.repositories.SessionsRepository
import ptystudybuddy.psb.repositories.StudentsRepository

@Service
class ReviewsService(
  private val reviewsRepository: ReviewsRepository,
  private val sessionsRepository: SessionsRepository,
  private val sessionAssignmentRepository: SessionAssignmentRepository,
  private val studentsRepository: StudentsRepository,
  private val authHelper: AuthHelper,
) {

  fun createReview(req: CreateReviewRequest): ResponseEntity<SuccessRes<String>> {
    val session =
      sessionsRepository.findByIdOrNull(req.sessionId)
        ?: throw EntityNotFoundException("No se encontró la sesión especificada")
    val tutor = sessionAssignmentRepository.findBySessionId(session)?.tutorId
    val student =
      studentsRepository.findByIdOrNull(authHelper.userId())
        ?: throw EntityNotFoundException("Este estudiante no existe")
    reviewsRepository.save(
      ReviewsEntity(
        comment = req.comment,
        rating = req.rating,
        sessionId = session,
        tutorId = tutor as TutorsEntity,
        studentId = student,
      )
    )

    return ResponseEntity.ok(
      SuccessRes(HttpStatus.CREATED.value(), "Se ha enviado su retroalimentación")
    )
  }

  fun getAllReviews(sessionId: String): ResponseEntity<SuccessRes<List<ReviewsDto>>> {

    val reviews =
      reviewsRepository.findAllBySessionId(sessionId).takeIf { it.isNotEmpty() }
        ?: throw EntityNotFoundException("No hay retroalimentaciones")

      val reviewsDto = reviews.map { it.toDto() }

    return ResponseEntity.ok(SuccessRes(HttpStatus.OK.value(), reviewsDto))
  }
}

package ptystudybuddy.psb.reviews

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.ReviewsEntity
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.ReviewsRepository

@Service
class ReviewsService(private val reviewsRepository: ReviewsRepository) {

  fun createReview(reviewData: ReviewsEntity): ResponseEntity<SuccessRes<String>> {

    reviewsRepository.save(reviewData)

    return ResponseEntity.ok(
      SuccessRes(HttpStatus.CREATED.value(), "Se ha enviado su retroalimentación")
    )
  }

  fun getAllReviews(sessionId: String): ResponseEntity<SuccessRes<List<ReviewsEntity?>>?> {

    val reviews =
      reviewsRepository.findAllBySessionId(sessionId).takeIf { it.isNotEmpty() }
        ?: throw EntityNotFoundException("No hay retroalimentaciones")

    return ResponseEntity.ok(SuccessRes(HttpStatus.OK.value(), reviews))
  }
}

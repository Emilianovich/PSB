package ptystudybuddy.psb.reviews

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ptystudybuddy.psb.entities.ReviewsEntity

@RestController
@PreAuthorize("hasRole('STUDENT')")
@RequestMapping("reviews")
class ReviewsController(private val reviewsService: ReviewsService) {

  @GetMapping("/{reviewId}")
  fun getReviewPerSession(@PathVariable sessionId: String) = reviewsService.getAllReviews(sessionId)

  @PostMapping
  fun createReview(@Valid @RequestBody review: CreateReviewRequest) = reviewsService.createReview(review)
}

data class CreateReviewRequest(
  val comment: String? = null,
  val rating: Int,
  val sessionId: String
)
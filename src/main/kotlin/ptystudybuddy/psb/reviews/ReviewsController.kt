package ptystudybuddy.psb.reviews

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController

@RequestMapping("reviews")
class ReviewsController(private val reviewsService: ReviewsService) {

    @PreAuthorize("hasRole('ADMIN') or hasRole('TUTOR')")
    @GetMapping("/{sessionId}")
  fun getReviewPerSession(@PathVariable sessionId: String) = reviewsService.getAllReviews(sessionId)

  @PreAuthorize("hasRole('STUDENT')")
  @PostMapping
  fun createReview(@Valid @RequestBody review: CreateReviewRequest) =
    reviewsService.createReview(review)
}



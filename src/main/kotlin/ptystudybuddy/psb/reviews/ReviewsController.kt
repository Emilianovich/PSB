package ptystudybuddy.psb.reviews

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ptystudybuddy.psb.entities.ReviewsEntity

@RestController
@PreAuthorize("hasRole('STUDENT')")
@RequestMapping("reviews")
class ReviewsController(private val reviewsService: ReviewsService) {

  @GetMapping("/{reviewId}") fun getAllReviews() = reviewsService.getAllReviews()

  @PostMapping
  fun createReview(@RequestBody review: ReviewsEntity) = reviewsService.createReview(review)
}

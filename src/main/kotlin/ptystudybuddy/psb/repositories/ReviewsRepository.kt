package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.ReviewsEntity

interface ReviewsRepository : JpaRepository<ReviewsEntity, Int>

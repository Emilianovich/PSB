package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ptystudybuddy.psb.entities.ReviewsEntity

interface ReviewsRepository : JpaRepository<ReviewsEntity, Int> {

  @Query("SELECT s FROM ReviewsEntity s WHERE s.session_id.id = :sessionId")
  fun findAllBySessionId(sessionId: String): List<ReviewsEntity>
}

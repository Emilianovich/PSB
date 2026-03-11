package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ptystudybuddy.psb.entities.ReviewsEntity

interface ReviewsRepository : JpaRepository<ReviewsEntity, Int> {

  @Query("SELECT s FROM ReviewsEntity s WHERE s.session_id = :sessionId")
  fun findAllBySessionId(@Param("sessionId") sessionId: String): List<ReviewsEntity>
}

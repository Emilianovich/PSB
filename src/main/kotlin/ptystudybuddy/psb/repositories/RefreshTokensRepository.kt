package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ptystudybuddy.psb.entities.RefreshTokensEntity

interface RefreshTokensRepository : JpaRepository<RefreshTokensEntity, String> {
  @Query(value = "SELECT *FROM refresh_tokens WHERE user_id= ?1", nativeQuery = true)
  fun findByUserIdAndToken(userId: String, token: String): RefreshTokensEntity?
}

package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.PendingTutorsEntity

interface PendingTutorsRepository : JpaRepository<PendingTutorsEntity, String> {
  fun findAllByApprovedIsNull(): List<PendingTutorsEntity>

  fun findBySocialId(socialId: String): PendingTutorsEntity?

  fun findByEmail(email: String): PendingTutorsEntity?

  fun existsByEmailAndBlacklistedAtIsNotNull(email: String): Boolean

  fun existsBySocialIdAndBlacklistedAtIsNotNull(socialId: String): Boolean

  fun findBySocialIdAndBlacklistedAtIsNull(socialId: String): PendingTutorsEntity?

  fun findByEmailAndBlacklistedAtIsNull(email: String): PendingTutorsEntity?
}

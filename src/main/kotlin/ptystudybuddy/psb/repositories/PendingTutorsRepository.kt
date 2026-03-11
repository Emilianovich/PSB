package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.PendingTutorsEntity

interface PendingTutorsRepository : JpaRepository<PendingTutorsEntity, String> {
  fun existsByEmailAndBlacklistedAtIsNotNull(email: String): Boolean

  fun existsBySocialIdAndBlacklistedAtIsNotNull(socialId: String): Boolean
  // TODO Add function for when tutor hasn't been blacklisted
}

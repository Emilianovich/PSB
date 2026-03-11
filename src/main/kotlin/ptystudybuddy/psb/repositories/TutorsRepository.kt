package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.TutorsEntity

interface TutorsRepository : JpaRepository<TutorsEntity, String> {
  fun findByEmail(email: String): TutorsEntity?

  fun findBySocialId(id: String): TutorsEntity?
}

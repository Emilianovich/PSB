package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ptystudybuddy.psb.entities.TutorsEntity

interface TutorsRepository :
  JpaRepository<TutorsEntity, String>, JpaSpecificationExecutor<TutorsEntity> {
  fun findByEmail(email: String): TutorsEntity?

  fun findBySocialId(id: String): TutorsEntity?
}

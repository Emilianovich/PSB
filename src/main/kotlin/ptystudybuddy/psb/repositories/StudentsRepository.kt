package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.StudentsEntity

interface StudentsRepository : JpaRepository<StudentsEntity, String> {
  fun findByEmail(email: String): StudentsEntity?

  fun findBySocialId(socialId: String): StudentsEntity?
}

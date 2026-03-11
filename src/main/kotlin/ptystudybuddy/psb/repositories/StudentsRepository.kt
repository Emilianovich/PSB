package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ptystudybuddy.psb.entities.StudentsEntity

interface StudentsRepository : JpaRepository<StudentsEntity, String> {
  fun findByEmail(email: String): StudentsEntity?

  // TODO Refactor later to remove the raw query

  @Query(value = "SELECT *FROM students WHERE social_id= ?1", nativeQuery = true)
  fun findBySocialId(socialId: String): StudentsEntity?
}

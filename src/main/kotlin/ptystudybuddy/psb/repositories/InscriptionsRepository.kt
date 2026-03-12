package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ptystudybuddy.psb.entities.InscriptionId
import ptystudybuddy.psb.entities.InscriptionsEntity
import ptystudybuddy.psb.entities.SessionsEntity

interface InscriptionsRepository : JpaRepository<InscriptionsEntity, InscriptionId> {
  @Query(
    value = "SELECT * FROM inscriptions WHERE session_id = ?1 AND student_id IN (?2)",
    nativeQuery = true,
  )
  fun findStudentsBySessionId(sessionId: String, studentId: List<String>): List<InscriptionsEntity>

  fun findBySessionId(sessionId: SessionsEntity): List<InscriptionsEntity>
}

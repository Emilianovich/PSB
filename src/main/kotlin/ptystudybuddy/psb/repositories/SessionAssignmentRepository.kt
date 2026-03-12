package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.SessionAssignmentEntity
import ptystudybuddy.psb.entities.SessionAssignmentId
import ptystudybuddy.psb.entities.SessionsEntity

interface SessionAssignmentRepository :
  JpaRepository<SessionAssignmentEntity, SessionAssignmentId> {
  fun findBySessionId(sessionId: SessionsEntity): SessionAssignmentEntity?
}

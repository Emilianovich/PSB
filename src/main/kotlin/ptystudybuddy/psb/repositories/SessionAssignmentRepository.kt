package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.SessionAssignmentEntity
import ptystudybuddy.psb.entities.SessionAssignmentId

interface SessionAssignmentRepository : JpaRepository<SessionAssignmentEntity, SessionAssignmentId>

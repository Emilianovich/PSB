package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.PendingTutorsEntity

interface PendingTutorsRepository : JpaRepository<PendingTutorsEntity, String>

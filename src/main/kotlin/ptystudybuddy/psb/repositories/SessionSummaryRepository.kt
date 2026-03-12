package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ptystudybuddy.psb.entities.SessionSummaryEntity

interface SessionSummaryRepository :
  JpaRepository<SessionSummaryEntity, Long>, JpaSpecificationExecutor<SessionSummaryEntity>

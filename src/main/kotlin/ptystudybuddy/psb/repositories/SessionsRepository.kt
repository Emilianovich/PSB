package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.SessionsEntity

interface SessionsRepository: JpaRepository<SessionsEntity, String>
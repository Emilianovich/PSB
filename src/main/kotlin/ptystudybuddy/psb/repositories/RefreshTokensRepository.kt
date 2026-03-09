package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.RefreshTokensEntity

interface RefreshTokensRepository: JpaRepository<RefreshTokensEntity, String>
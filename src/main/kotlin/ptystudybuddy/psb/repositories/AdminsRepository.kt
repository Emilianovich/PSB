package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.AdminsEntity

interface AdminsRepository : JpaRepository<AdminsEntity, String>

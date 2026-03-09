package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.ClassroomsEntity

interface ClassroomsRepository: JpaRepository<ClassroomsEntity, String>
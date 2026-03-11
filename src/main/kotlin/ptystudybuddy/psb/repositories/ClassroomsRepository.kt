package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.ClassroomsEntity

interface ClassroomsRepository : JpaRepository<ClassroomsEntity, String> {

  fun findByIdIn(ids: List<String>): List<ClassroomsEntity>

  fun findAllByIdIn(ids: List<String>): List<ClassroomsEntity>
}

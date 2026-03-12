package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.AvailabilityEntity

interface AvailabilityRepository : JpaRepository<AvailabilityEntity, String> {
  fun existsByIdAndSelected(id: String, selected: Boolean): Boolean

  fun findByIdIn(ids: List<String>): List<AvailabilityEntity>
}

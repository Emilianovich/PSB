package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.AvailabilityEntity

interface AvailabilityRepository : JpaRepository<AvailabilityEntity, String>

package ptystudybuddy.psb.repositories

import java.util.Optional
import org.springframework.data.repository.Repository
import ptystudybuddy.psb.entities.SchedulesEntity

interface SchedulesRepository : Repository<SchedulesEntity, String> {
  fun existsById(id: String): Boolean

  fun findById(id: String): Optional<SchedulesEntity>

  fun findAll(): List<SchedulesEntity>
}

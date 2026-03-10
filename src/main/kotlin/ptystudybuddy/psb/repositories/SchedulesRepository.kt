package ptystudybuddy.psb.repositories

import org.springframework.data.repository.Repository
import ptystudybuddy.psb.entities.SchedulesEntity
import java.util.Optional

interface SchedulesRepository : Repository<SchedulesEntity, String>{
    fun existsById(id: String): Boolean
    fun findById(id: String): Optional<SchedulesEntity>
    fun findAll(): List<SchedulesEntity>
}

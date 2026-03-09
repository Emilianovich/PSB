package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.SchedulesEntity

interface SchedulesRepository: JpaRepository<SchedulesEntity, Int>
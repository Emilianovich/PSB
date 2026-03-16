package ptystudybuddy.psb.schedules

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.ScheduleRes
import ptystudybuddy.psb.entities.toScheduleRes
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.SchedulesRepository

@Service
class ScheduleService(private val schedulesRepository: SchedulesRepository) {
  fun getAll(): ResponseEntity<SuccessRes<List<ScheduleRes>>> {
    val rawSchedules =
      schedulesRepository.findAll().takeIf { it.isNotEmpty() }
        ?: throw EntityNotFoundException("No hay horarios disponibles")
    val schedules = rawSchedules.map { it.toScheduleRes() }
    return ResponseEntity.ok()
      .body(SuccessRes(statusCode = HttpStatus.OK.value(), content = schedules))
  }
}

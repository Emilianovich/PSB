package ptystudybuddy.psb.availability

import jakarta.persistence.EntityNotFoundException
import java.time.LocalDate
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.AvailabilityEntity
import ptystudybuddy.psb.exceptions.customs.UnprocessableEntityException
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.AvailabilityRepository
import ptystudybuddy.psb.repositories.ClassroomsRepository
import ptystudybuddy.psb.repositories.SchedulesRepository

@Service
class AvailabilityService(
  val availabilityRepository: AvailabilityRepository,
  val classroomsRepository: ClassroomsRepository,
  val schedulesRepository: SchedulesRepository,
) {

  fun getAllAvailabilities(): ResponseEntity<SuccessRes<List<AvailabilityDto>>?> {

    val availabilityBlocks =
      availabilityRepository.findAll().takeIf { it.isNotEmpty() }
        ?: throw EntityNotFoundException("No se encontraron sesiones disponibles")

    return ResponseEntity.ok(
      SuccessRes(HttpStatus.OK.value(), availabilityBlocks.map { it.toResponseDto() })
    )
  }

  fun createAvailability(
    availabilityData: List<AvailabilityDto>
  ): ResponseEntity<SuccessRes<String>> {

    val todayDates = availabilityData.filter { it.date.isEqual(LocalDate.now()) }
    if (todayDates.isNotEmpty()) {
      val fields = todayDates.joinToString { it.date.toString() }
      throw UnprocessableEntityException("No se puede usar la fecha de hoy: $fields")
    }

    val classrooms =
      classroomsRepository.findAllByIdIn(availabilityData.map { it.class_id }).associateBy { it.id }

    val missingClassrooms = availabilityData.map { it.class_id }.filter { it !in classrooms.keys }

    if (missingClassrooms.isNotEmpty()) {
      throw DataIntegrityViolationException("No existen los salones: $missingClassrooms")
    }

    val schedules =
      schedulesRepository.findAllByIdIn(availabilityData.map { it.schedule_id }).associateBy {
        it.id
      }

    val missingSchedules = availabilityData.map { it.schedule_id }.filter { it !in schedules.keys }
    if (missingSchedules.isNotEmpty()) {
      throw DataIntegrityViolationException("No existen los horarios: $missingSchedules")
    }

    val ids = availabilityData.map { "${it.class_id}_${it.schedule_id}_${it.date}" }

    availabilityRepository
      .findByIdIn(ids)
      .takeIf { it.isNotEmpty() }
      ?.let { it ->
        throw DataIntegrityViolationException(
          "Ya existe una disponibilidad en ${it.map {
                            "(Salón: ${it.class_id.id} Horario: ${it.schedule_id.id} Fecha: ${it.date})" }}"
        )
      }

    val availabilityBlocks =
      availabilityData.map { dto ->
        val classroom =
          classrooms[dto.class_id]
            ?: throw IllegalArgumentException("Classroom ${dto.class_id} not found")
        val schedule =
          schedules[dto.schedule_id]
            ?: throw IllegalArgumentException("Schedule ${dto.schedule_id} not found")

        AvailabilityEntity(
          id = "${dto.class_id}_${dto.schedule_id}_${dto.date}",
          class_id = classroom,
          schedule_id = schedule,
          date = dto.date,
        )
      }

    availabilityRepository.saveAll(availabilityBlocks)

    return ResponseEntity.ok(
      SuccessRes(
        HttpStatus.CREATED.value(),
        if (availabilityData.size > 1) "Disponibilidades creadas con éxito"
        else "Disponibilidad creada con éxito",
      )
    )
  }
}

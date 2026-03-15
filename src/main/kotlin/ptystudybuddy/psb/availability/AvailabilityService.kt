package ptystudybuddy.psb.availability

import jakarta.persistence.EntityNotFoundException
import java.time.LocalDate
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.AvailabilityEntity
import ptystudybuddy.psb.exceptions.customs.UnprocessableEntityException
import ptystudybuddy.psb.helpers.DateAndTimeHelper
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

  fun getAllAvailabilities(): ResponseEntity<SuccessRes<List<AvailabilityRes>>> {

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

    val dateNow = LocalDate.now()
    val todayDates = availabilityData.filter { !it.date.isAfter(dateNow) }
    if (todayDates.isNotEmpty()) {
      val fields = todayDates.joinToString { it.date.toString() }
      throw UnprocessableEntityException(
        "No se puede usar la fecha de hoy ni una fecha anterior: $fields"
      )
    }

    val classrooms =
      classroomsRepository.findAllByIdIn(availabilityData.map { it.classId }).associateBy { it.id }

    val missingClassrooms = availabilityData.map { it.classId }.filter { it !in classrooms.keys }

    if (missingClassrooms.isNotEmpty()) {
      throw DataIntegrityViolationException("No existen los salones: $missingClassrooms")
    }

    val schedules =
      schedulesRepository.findAllByIdIn(availabilityData.map { it.scheduleId }).associateBy {
        it.id
      }

    val missingSchedules = availabilityData.map { it.scheduleId }.filter { it !in schedules.keys }
    if (missingSchedules.isNotEmpty()) {
      throw DataIntegrityViolationException("No existen los horarios: $missingSchedules")
    }

    val ids = availabilityData.map { "${it.classId}_${it.scheduleId}_${it.date}" }

    availabilityRepository
      .findByIdIn(ids)
      .takeIf { it.isNotEmpty() }
      ?.let { it ->
        throw DataIntegrityViolationException(
          "Ya existe una disponibilidad en ${it.map {
                            "(Salón: ${it.classId.id} Horario: ${it.scheduleId.id} Fecha: ${it.date})" }}"
        )
      }

    val availabilityBlocks =
      availabilityData.map { dto ->
        val classroom =
          classrooms[dto.classId]
            ?: throw IllegalArgumentException("Classroom ${dto.classId} not found")
        val schedule =
          schedules[dto.scheduleId]
            ?: throw IllegalArgumentException("Schedule ${dto.scheduleId} not found")

        AvailabilityEntity(
          id = "${dto.classId}_${dto.scheduleId}_${dto.date}",
          classId = classroom,
          scheduleId = schedule,
          date = dto.date,
          endDatetime = DateAndTimeHelper.createDateTime(dto.date, schedule.endTime),
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

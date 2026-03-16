package ptystudybuddy.psb.admins

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.toDto
import ptystudybuddy.psb.helpers.AuthHelper
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.AdminsRepository

@Service
class AdminsService(
  private val adminsRepository: AdminsRepository,
  private val authHelper: AuthHelper,
) {

  fun getProfile(): ResponseEntity<SuccessRes<AdminsDto>> {

    val userId = authHelper.userId()
    val admin =
      adminsRepository
        .findById(userId)
        .orElseThrow { EntityNotFoundException("Admin con id: $userId no encontrado") }
        .toDto()

    return ResponseEntity.ok(SuccessRes(HttpStatus.OK.value(), admin))
  }
}

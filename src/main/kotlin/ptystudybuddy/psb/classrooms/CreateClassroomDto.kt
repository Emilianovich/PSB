package ptystudybuddy.psb.classrooms

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateClassroomDto(
  @field:NotBlank(message = "El id del salón es requerido")
  @field:Size(max = 5)
  @field:Pattern(regexp = "^[1-4]-[0-9]{3}$")
  val id: String,
  @field:NotBlank(message = "La ubicación no puede quedar vacía")
  @field:Size(min = 20, message = "La ubicación no puede tener menos de 20 caracteres")
  @field:Size(max = 100, message = "La ubicación no puede tener más de 100 caracteres")
  val location: String,
)

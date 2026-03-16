package ptystudybuddy.psb.annotations.email

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ptystudybuddy.psb.repositories.PendingTutorsRepository
import ptystudybuddy.psb.repositories.StudentsRepository
import ptystudybuddy.psb.repositories.TutorsRepository

class EmailExistsValidation(
  private val studentsRepository: StudentsRepository,
  private val tutorsRepository: TutorsRepository,
  private val pendingTutorsRepository: PendingTutorsRepository,
) : ConstraintValidator<EmailExists, String> {
  override fun isValid(email: String, context: ConstraintValidatorContext): Boolean {
    email.takeIf {
      studentsRepository.findByEmail(it) == null &&
        tutorsRepository.findByEmail(it) == null &&
        pendingTutorsRepository.findByEmail(it) == null
    }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("Ya existe un usuario con ese correo")
          .addConstraintViolation()
        return false
      }
    return true
  }
}

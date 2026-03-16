package ptystudybuddy.psb.annotations.tutors.exists.email

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ptystudybuddy.psb.repositories.AdminsRepository
import ptystudybuddy.psb.repositories.PendingTutorsRepository
import ptystudybuddy.psb.repositories.StudentsRepository
import ptystudybuddy.psb.repositories.TutorsRepository

class TutorEmailExistsValidation(
  private val pendingTutorsRepository: PendingTutorsRepository,
  private val tutorsRepository: TutorsRepository,
  private val studentsRepository: StudentsRepository,
  private val adminsRepository: AdminsRepository,
) : ConstraintValidator<TutorEmailExistsValidator, String> {
  override fun isValid(email: String, context: ConstraintValidatorContext): Boolean {
    email.takeUnless { pendingTutorsRepository.existsByEmailAndBlacklistedAtIsNotNull(it) }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("Ya existe un tutor con ese correo")
          .addConstraintViolation()
        return false
      }
    val isUniqueEmail =
      studentsRepository.findByEmail(email) == null &&
        tutorsRepository.findByEmail(email) == null &&
        adminsRepository.findByEmail(email) == null
    if (!isUniqueEmail) {
      context.disableDefaultConstraintViolation()
      context
        .buildConstraintViolationWithTemplate("Ya existe un usuario con ese correo")
        .addConstraintViolation()
      return false
    }
    return true
  }
}

package ptystudybuddy.psb.annotations.tutors.exists.email

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ptystudybuddy.psb.repositories.PendingTutorsRepository
import ptystudybuddy.psb.repositories.TutorsRepository

class TutorEmailExistsValidation(
  private val pendingTutorsRepository: PendingTutorsRepository,
  private val tutorsRepository: TutorsRepository,
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

    email.takeIf { tutorsRepository.findByEmail(it) == null }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("Ya existe un tutor con ese correo")
          .addConstraintViolation()
        return false
      }
    return true
  }
}

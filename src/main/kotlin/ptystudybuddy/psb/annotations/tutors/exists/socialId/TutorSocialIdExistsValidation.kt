package ptystudybuddy.psb.annotations.tutors.exists.socialId

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ptystudybuddy.psb.repositories.PendingTutorsRepository
import ptystudybuddy.psb.repositories.TutorsRepository

class TutorSocialIdExistsValidation(
  private val pendingTutorsRepository: PendingTutorsRepository,
  private val tutorsRepository: TutorsRepository,
) : ConstraintValidator<TutorSocialIdExistsValidator, String> {
  override fun isValid(socialId: String, context: ConstraintValidatorContext): Boolean {
    socialId.takeUnless { pendingTutorsRepository.existsBySocialIdAndBlacklistedAtIsNotNull(it) }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("Ya existe un tutor con esa cédula")
          .addConstraintViolation()
        return false
      }

    socialId.takeUnless { tutorsRepository.findBySocialId(it) != null }
      ?: run {
        println("In the social id validation for tutors")
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("Ya existe un tutor con esa cédula")
          .addConstraintViolation()
        return false
      }
    return true
  }
}

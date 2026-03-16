package ptystudybuddy.psb.annotations.tutors.exists.socialId

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ptystudybuddy.psb.repositories.AdminsRepository
import ptystudybuddy.psb.repositories.PendingTutorsRepository
import ptystudybuddy.psb.repositories.StudentsRepository
import ptystudybuddy.psb.repositories.TutorsRepository

class TutorSocialIdExistsValidation(
  private val pendingTutorsRepository: PendingTutorsRepository,
  private val tutorsRepository: TutorsRepository,
  private val studentsRepository: StudentsRepository,
  private val adminsRepository: AdminsRepository,
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
    val isUniqueSocialId =
      studentsRepository.findBySocialId(socialId) == null &&
        tutorsRepository.findBySocialId(socialId) == null &&
        adminsRepository.findBySocialId(socialId) == null
    if (!isUniqueSocialId) {
      context.disableDefaultConstraintViolation()
      context
        .buildConstraintViolationWithTemplate("Ya existe un usuario con esa cédula")
        .addConstraintViolation()
      return false
    }
    return true
  }
}

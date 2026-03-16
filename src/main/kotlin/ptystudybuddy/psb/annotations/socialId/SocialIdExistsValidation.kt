package ptystudybuddy.psb.annotations.socialId

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ptystudybuddy.psb.repositories.AdminsRepository
import ptystudybuddy.psb.repositories.PendingTutorsRepository
import ptystudybuddy.psb.repositories.StudentsRepository
import ptystudybuddy.psb.repositories.TutorsRepository

class SocialIdExistsValidation(
  private val studentsRepository: StudentsRepository,
  private val tutorsRepository: TutorsRepository,
  private val adminsRepository: AdminsRepository,
  private val pendingTutorsRepository: PendingTutorsRepository,
) : ConstraintValidator<SocialIdExists, String> {
  override fun isValid(socialId: String, context: ConstraintValidatorContext): Boolean {
    socialId.takeIf {
      studentsRepository.findBySocialId(it) == null &&
        tutorsRepository.findBySocialId(it) == null &&
        adminsRepository.findBySocialId(it) == null &&
        pendingTutorsRepository.findBySocialId(it) == null
    }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("Ya existe un usuario con esa cédula")
          .addConstraintViolation()
        return false
      }
    return true
  }
}

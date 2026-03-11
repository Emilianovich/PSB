package ptystudybuddy.psb.annotations.student.exists.socialId

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ptystudybuddy.psb.repositories.StudentsRepository

class StudentSocialIdExistsValidation(private val studentsRepository: StudentsRepository) :
  ConstraintValidator<StudentSocialIdExistsValidator, String> {
  override fun isValid(email: String, context: ConstraintValidatorContext): Boolean {
    email.takeIf { studentsRepository.findBySocialId(it) == null }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("Ya existe un estudiante con esa cédula")
          .addConstraintViolation()
        return false
      }
    return true
  }
}

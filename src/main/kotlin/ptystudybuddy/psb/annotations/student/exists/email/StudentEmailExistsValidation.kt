package ptystudybuddy.psb.annotations.student.exists.email

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ptystudybuddy.psb.repositories.StudentsRepository

class StudentEmailExistsValidation(private val studentsRepository: StudentsRepository) :
  ConstraintValidator<StudentEmailExistsValidator, String> {
  override fun isValid(email: String, context: ConstraintValidatorContext): Boolean {
    email.takeIf { studentsRepository.findByEmail(it) == null }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("Ya existe un estudiante con ese correo")
          .addConstraintViolation()
        return false
      }
    return true
  }
}

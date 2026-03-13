package ptystudybuddy.psb.annotations.sessionStatus

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ptystudybuddy.psb.entities.SessionStatus

class SessionStatusValidation : ConstraintValidator<SessionStatusValidator, String> {
  override fun isValid(status: String?, context: ConstraintValidatorContext): Boolean {
    if (status == null) return true
    if (
      status !== SessionStatus.ACTIVE.name &&
        status !== SessionStatus.NOT_ACTIVE.name &&
        status !== SessionStatus.CANCELLED.name
    ) {
      return false
    }
    return true
  }
}

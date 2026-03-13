package ptystudybuddy.psb.annotations.sessionStatus

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Constraint(validatedBy = [SessionStatusValidation::class])
annotation class SessionStatusValidator(
  val message: String = "Una sesión no puede tener el estado suministrado",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

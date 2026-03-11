package ptystudybuddy.psb.annotations.student.exists.socialId

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [StudentSocialIdExistsValidation::class])
annotation class StudentSocialIdExistsValidator(
  val message: String = "",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

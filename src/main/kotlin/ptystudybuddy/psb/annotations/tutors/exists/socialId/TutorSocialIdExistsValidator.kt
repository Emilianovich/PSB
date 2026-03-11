package ptystudybuddy.psb.annotations.tutors.exists.socialId

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [TutorSocialIdExistsValidation::class])
annotation class TutorSocialIdExistsValidator(
  val message: String = "",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

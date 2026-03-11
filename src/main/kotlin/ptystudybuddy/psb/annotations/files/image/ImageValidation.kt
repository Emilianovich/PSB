package ptystudybuddy.psb.annotations.files.image

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.util.unit.DataSize
import org.springframework.web.multipart.MultipartFile

class ImageValidation : ConstraintValidator<ImageValidator, MultipartFile> {
  override fun isValid(image: MultipartFile, context: ConstraintValidatorContext): Boolean {
    val maxSize = DataSize.ofMegabytes(5).toBytes()
    val contentType = Regex("^image/(jpe?g|png|gif|bmp|svg\\+xml|webp|ico|tiff|avif)$")
    // Needed because Spring always creates an empty MultipartFile object
    image.takeUnless { it.isEmpty }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("Imagen no suministrada")
          .addConstraintViolation()
        return false
      }

    image.takeIf { it.size < maxSize }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("La imagen no puede exceder los 5MB")
          .addConstraintViolation()
        return false
      }

    image.takeIf { it.contentType.toString().matches(contentType) }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate(
            "Los tipos de imagen válidos son: jpg, jpeg, png, gif, bmp, svg, webp, ico, tiff o avif"
          )
          .addConstraintViolation()
        return false
      }
    return true
  }
}

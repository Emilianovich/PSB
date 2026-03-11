package ptystudybuddy.psb.annotations.files.pdf

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.util.unit.DataSize
import org.springframework.web.multipart.MultipartFile

class PDFValidation : ConstraintValidator<PDFValidator, MultipartFile> {
  override fun isValid(cv: MultipartFile, context: ConstraintValidatorContext): Boolean {
    val maxSize = DataSize.ofMegabytes(5).toBytes()
    val contentType = Regex("^application/pdf$")
    println("Your cv $cv")
    // Needed because Spring always creates an empty MultipartFile object
    cv.takeUnless { it.isEmpty }
      ?: run {
        context.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate("CV no suministrado").addConstraintViolation()
        return false
      }

    cv.takeUnless { it.size > maxSize }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("El CV no puede exceder los 5MB")
          .addConstraintViolation()
        return false
      }

    cv.takeIf { it.contentType.toString().matches(contentType) }
      ?: run {
        context.disableDefaultConstraintViolation()
        context
          .buildConstraintViolationWithTemplate("El CV suministrado no es un PDF")
          .addConstraintViolation()
        return false
      }
    return true
  }
}

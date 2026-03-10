package ptystudybuddy.psb.exceptions

import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ptystudybuddy.psb.exceptions.customs.UnprocessableEntityException
import ptystudybuddy.psb.presentation.ExceptionRes

@RestControllerAdvice
class GlobalExceptionHandler {
    // Status Code 400 when DTO validation fails
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleBadRequest(e: MethodArgumentNotValidException): ResponseEntity<ExceptionRes<List<Map<String, String?>>>> {
        val errors =
            e.bindingResult.fieldErrors.map {
                mapOf(
                    "field" to it.field,
                    "message" to it.defaultMessage,
                )
            }
        val body =
            mapOf(
                "success" to false,
                "statusCode" to HttpStatus.BAD_REQUEST.value(),
                "content" to errors,
            )
        return ResponseEntity.badRequest().body(ExceptionRes(statusCode = e.statusCode.value(), errors = errors))
    }

    // Errors of JSON not formed accordingly, status code 400, 404, 409, 422
    @ExceptionHandler(
        HttpMessageNotReadableException::class,
        AccessDeniedException::class,
        EntityNotFoundException::class,
        DataIntegrityViolationException::class,
        UnprocessableEntityException::class,
    )
    fun handleAccessDeniedException(ex: Exception): ResponseEntity<ExceptionRes<String>> {
        val status =
            when (ex) {
                is HttpMessageNotReadableException -> HttpStatus.BAD_REQUEST
                is AccessDeniedException -> HttpStatus.FORBIDDEN
                is EntityNotFoundException -> HttpStatus.NOT_FOUND
                is DataIntegrityViolationException -> HttpStatus.CONFLICT
                is UnprocessableEntityException -> HttpStatus.UNPROCESSABLE_ENTITY
                else -> HttpStatus.BAD_REQUEST
            }
        return ResponseEntity.status(status.value())
            .body(ExceptionRes(statusCode = status.value(), errors = ex.message ?: "Un error del tipo ${status.name} ocurrió"))
    }

    // Status Code 415
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleUnsupportedException(ex: HttpMediaTypeNotSupportedException): ResponseEntity<ExceptionRes<String>> {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).body(
            ExceptionRes(
                statusCode = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                errors = ex.message ?: "Un error del tipo ${ex.cause} ocurrió",
            ),
        )
    }

    // Status Code 500
    @ExceptionHandler(Exception::class)
    fun handleInternalException(e: Exception): ResponseEntity<ExceptionRes<String>> {
        return ResponseEntity.internalServerError().body(
            ExceptionRes(
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                errors = e.message ?: "Error interno del servidor",
            ),
        )
    }
}

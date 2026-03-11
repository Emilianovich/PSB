package ptystudybuddy.psb.helpers

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class AuthHelper(private val req: HttpServletRequest) {
  fun userId(): String {
    val auth =
      SecurityContextHolder.getContext().authentication
        ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usted no está autenticado")
    return auth.principal as String
  }

  fun refreshToken(): String {
    val refreshToken = req.cookies?.find { it.name == "refreshToken" }?.value
    if (refreshToken != null) {
      return refreshToken
    }
    throw ResponseStatusException(
      HttpStatus.UNAUTHORIZED,
      "Usted no está autorizado, token inválido",
    )
  }

  fun accessToken(): String {
    val accessToken = req.cookies?.find { it.name == "accessToken" }?.value
    if (accessToken != null) {
      return accessToken
    }
    throw ResponseStatusException(
      HttpStatus.UNAUTHORIZED,
      "Usted no está autorizado, token inválido",
    )
  }

  fun userRole(): String {
    val auth =
      SecurityContextHolder.getContext().authentication.authorities
        ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usted no está autenticado")
    return auth.toList().first().toString().removePrefix("ROLE_")
  }
}

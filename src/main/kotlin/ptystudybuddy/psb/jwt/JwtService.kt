package ptystudybuddy.psb.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.Duration
import java.util.Base64
import java.util.Date
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ptystudybuddy.psb.exceptions.customs.UnprocessableEntityException

@Service
class JwtService(@Value("\${secret.key}") private val rawKey: String) {
  private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(rawKey))

  // FIXME CHANGE FOR PROD BACK TO 2 MIN
  // TODO Change to app.properties values
  private val accessTokenValidity = Duration.ofMinutes(50).toMillis()
  val refreshTokenValidity = Duration.ofDays(7).toMillis()

  private fun generateToken(userId: String, type: String, expiry: Long, role: String): String {
    val now = Date()
    val expiryDate = Date(now.time + expiry)
    return Jwts.builder()
      .subject(userId)
      .claim("type", type)
      .claim("role", role)
      .issuedAt(now)
      .expiration(expiryDate)
      .signWith(secretKey, Jwts.SIG.HS256)
      .compact()
  }

  // Extract payload from token
  private fun parseAllClaims(token: String): Claims? {
    return try {
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
    } catch (e: JwtException) {
      println("Error ocurrido al verificar el token: ${e.message}")
      null
    }
  }

  fun generateAccessToken(userId: String, role: String): String {
    return generateToken(userId, "access", accessTokenValidity, role)
  }

  fun generateRefreshToken(userId: String, role: String): String {
    return generateToken(userId, "refresh", refreshTokenValidity, role)
  }

  fun validateAccessToken(token: String): Boolean {
    val claims = parseAllClaims(token) ?: return false
    val tokenType = claims["type"] as? String ?: return false
    return tokenType == "access"
  }

  fun validateRefreshToken(token: String): Boolean {
    val claims = parseAllClaims(token) ?: return false
    val tokenType = claims["type"] as? String ?: return false
    return tokenType == "refresh"
  }

  fun getUserIdFromToken(token: String): String {
    val claims =
      parseAllClaims(token) ?: throw UnprocessableEntityException("Token alterado, no es válido")
    return claims.subject
  }

  fun getUserRoleFromToken(token: String): String {
    val claims =
      parseAllClaims(token) ?: throw UnprocessableEntityException("Token alterado, no es válido")
    return claims["role"] as String
  }
}

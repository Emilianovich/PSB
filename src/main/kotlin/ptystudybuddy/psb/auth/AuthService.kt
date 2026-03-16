package ptystudybuddy.psb.auth

import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Base64
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.RefreshTokensEntity
import ptystudybuddy.psb.exceptions.customs.UnprocessableEntityException
import ptystudybuddy.psb.helpers.AuthHelper
import ptystudybuddy.psb.jwt.JwtService
import ptystudybuddy.psb.presentation.SuccessRes
import ptystudybuddy.psb.repositories.AdminsRepository
import ptystudybuddy.psb.repositories.PendingTutorsRepository
import ptystudybuddy.psb.repositories.RefreshTokensRepository
import ptystudybuddy.psb.repositories.StudentsRepository
import ptystudybuddy.psb.repositories.TutorsRepository
import ptystudybuddy.psb.security.BCryptService

@Service
class AuthService(
  private val bCryptPasswordEncoder: BCryptService,
  private val jwtService: JwtService,
  private val pendingTutorsRepository: PendingTutorsRepository,
  private val studentsRepository: StudentsRepository,
  private val adminsRepository: AdminsRepository,
  private val tutorsRepository: TutorsRepository,
  private val res: HttpServletResponse,
  private val authHelper: AuthHelper,
  private val refreshTokensRepository: RefreshTokensRepository,
) {
  private fun <T : HasEmailAndPassword> validateUser(
    entity: T,
    entityId: String?,
    entityRole: String,
    entityFullName: String,
    plainPassword: String,
  ): ResponseEntity<SuccessRes<String>> {
    val id =
      entityId.takeIf { it != null }
        ?: throw UnprocessableEntityException("Correo o contraseña incorrectos")
    if (bCryptPasswordEncoder.matches(plainPassword, entity.password)) {
      val accessToken = jwtService.generateAccessToken(id, entityRole)
      val refreshToken = jwtService.generateRefreshToken(id, entityRole)
      storeRefreshToken(refreshToken)
      setCookies(accessToken, refreshToken)
      return ResponseEntity.ok(
        SuccessRes(statusCode = HttpStatus.OK.value(), content = "Bienvenido $entityFullName")
      )
    }
    throw AccessDeniedException("Correo o contraseña incorrectos")
  }

  fun login(req: LoginReq): ResponseEntity<SuccessRes<String>> {
    val adminRegex = Regex("^[a-zA-Z0-9._%+\\-]+@ptystudybuddy\\.dev$")
    if (req.email.matches(adminRegex)) {
      val admin =
        adminsRepository.findByEmail(req.email)
          ?: throw AccessDeniedException("Correo o contraseña incorrectos")
      return validateUser(admin, admin.id, admin.role, admin.fullname, req.password)
    }
    val tutor =
      tutorsRepository.findByEmail(req.email)
        ?: throw AccessDeniedException("Correo o contraseña incorrectos")
    return validateUser(tutor, tutor.id, tutor.role, tutor.fullname, req.password)
  }

  fun studentLogin(req: LoginReq): ResponseEntity<SuccessRes<String>> {
    val student =
      studentsRepository.findByEmail(req.email)
        ?: throw AccessDeniedException("Correo o contraseña incorrectos")
    return validateUser(student, student.id, student.role, student.fullname, req.password)
  }

  private fun setCookies(accessToken: String, refreshToken: String) {
    res.addCookie(
      Cookie("accessToken", accessToken).apply {
        isHttpOnly = true
        secure = false
        path = "/"
        // FIXME CHANGE ME FOR PROD
        maxAge = 3000
      }
    )
    res.addCookie(
      Cookie("refreshToken", refreshToken).apply {
        isHttpOnly = true
        secure = false
        path = "/"
        maxAge = 604800
      }
    )
  }

  fun refreshAccessToken(): ResponseEntity<SuccessRes<String>> {
    val currentRefreshToken = authHelper.refreshToken()
    val newAccessToken =
      jwtService.generateAccessToken(
        jwtService.getUserIdFromToken(currentRefreshToken),
        jwtService.getUserRoleFromToken(currentRefreshToken),
      )
    res.addCookie(
      Cookie("accessToken", newAccessToken).apply {
        isHttpOnly = true
        secure = false
        path = "/"
        // FIXME CHANGE ME FOR PROD
        maxAge = 3000
      }
    )
    return ResponseEntity.ok(
      SuccessRes(statusCode = HttpStatus.OK.value(), content = "Token de acceso refrescado")
    )
  }

  fun logout(): ResponseEntity<SuccessRes<String>> {
    val accessToken = authHelper.accessToken()
    val refreshToken = authHelper.refreshToken()
    findCurrentRefreshToken(authHelper.userId(), authHelper.refreshToken())
    res.addCookie(
      Cookie("accessToken", accessToken).apply {
        isHttpOnly = true
        secure = false
        path = "/"
        maxAge = 0
      }
    )
    res.addCookie(
      Cookie("refreshToken", refreshToken).apply {
        isHttpOnly = true
        secure = false
        path = "/"
        maxAge = 0
      }
    )
    return ResponseEntity.ok(
      SuccessRes(statusCode = HttpStatus.OK.value(), content = "Sesión cerrada exitosamente")
    )
  }

  private fun hashRefreshToken(rawToken: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(rawToken.toByteArray())
    return Base64.getEncoder().encodeToString(hashBytes)
  }

  private fun storeRefreshToken(rawToken: String) {
    val userRole = jwtService.getUserRoleFromToken(rawToken)
    val userId = jwtService.getUserIdFromToken(rawToken)
    if (userRole == "ADMIN") {
      adminsRepository.findByIdOrNull(userId)
        ?: throw EntityNotFoundException("Usuario no encontrado")
      handleRefreshTokenSave(userId, userRole, rawToken)
      return
    }
    if (userRole == "TUTOR") {
      tutorsRepository.findByIdOrNull(userId)
        ?: throw EntityNotFoundException("Usuario no encontrado")
      handleRefreshTokenSave(userId, userRole, rawToken)
      return
    }
    studentsRepository.findByIdOrNull(userId)
      ?: throw EntityNotFoundException("Usuario no encontrado")
    handleRefreshTokenSave(userId, userRole, rawToken)
  }

  private fun findCurrentRefreshToken(userId: String, token: String) {
    val currentToken = refreshTokensRepository.findByUserIdAndToken(userId, token) ?: return
    // Casting as String because it's obvious it exists
    refreshTokensRepository.deleteById(currentToken.id as String)
  }

  private fun handleRefreshTokenSave(userId: String, userRole: String, rawToken: String) {
    val hashedToken = hashRefreshToken(rawToken)
    // Converting to Epoch Milli and creating an instance of Instant for later
    val expiresAt =
      Instant.ofEpochMilli(Instant.now().plusMillis(jwtService.refreshTokenValidity).toEpochMilli())
    val expiryTimestamp = LocalDateTime.ofInstant(expiresAt, ZoneOffset.UTC)
    findCurrentRefreshToken(userId, rawToken)
    refreshTokensRepository.save(
      RefreshTokensEntity(
        token = hashedToken,
        expiry = expiryTimestamp,
        userId = userId,
        role = userRole,
      )
    )
  }

  private fun <T : HasEmailAndPassword> saveNewPassword(
    entity: T,
    newPassword: String,
    patch: (T) -> Unit,
  ) {
    entity.password = validateAndHashNewPassword(entity.password, newPassword)
    patch(entity)
  }

  fun handlePasswordPatch(req: PatchPasswordReq): ResponseEntity<SuccessRes<String>> {
    val role = authHelper.userRole()
    val id = authHelper.userId()
    if (req.password != req.confirmPassword) {
      throw IllegalArgumentException(
        "Las contraseñas ingresadas no coinciden. Por favor verifíquelas e inténtelo nuevamente"
      )
    }
    when (role) {
      "ADMIN" -> {
        val admin =
          adminsRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("Usuario no encontrado")
        saveNewPassword(admin, req.password) { adminsRepository.save(it) }
      }
      "TUTOR" -> {
        val tutor =
          tutorsRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("Usuario no encontrado")
        saveNewPassword(tutor, req.password) { tutorsRepository.save(it) }
      }
      else -> {
        val student =
          studentsRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("Usuario no encontrado")
        saveNewPassword(student, req.password) { studentsRepository.save(it) }
      }
    }
    return ResponseEntity.ok(
      SuccessRes(
        statusCode = HttpStatus.OK.value(),
        content = "Su contraseña fue cambiada exitosamente",
      )
    )
  }

  private fun validateAndHashNewPassword(hash: String, password: String): String {
    password.takeUnless { bCryptPasswordEncoder.matches(it, hash) }
      ?: throw IllegalArgumentException("Su nueva contraseña no puede ser igual a la anterior")
    return bCryptPasswordEncoder.encode(password)
  }
}

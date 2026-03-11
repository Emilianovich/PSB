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
import ptystudybuddy.psb.entities.PendingTutorsEntity
import ptystudybuddy.psb.entities.RefreshTokensEntity
import ptystudybuddy.psb.entities.StudentsEntity
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
  fun login(req: LoginReq): ResponseEntity<SuccessRes<String>> {
    val adminRegex = Regex("^[a-zA-Z0-9._%+\\-]+@ptystudybuddy\\.dev$")
    if (req.email.matches(adminRegex)) {
      val admin =
        adminsRepository.findByEmail(req.email)
          ?: throw AccessDeniedException("Correo o contraseña incorrectos")
      admin.takeIf { bCryptPasswordEncoder.matches(req.password, admin.password) }
        ?: throw AccessDeniedException("Correo o contraseña incorrectos")

      val id = admin.id ?: throw IllegalArgumentException("Correo o contraseña incorrectos")
      val accessToken = jwtService.generateAccessToken(id, admin.role)
      val refreshToken = jwtService.generateRefreshToken(admin.id, admin.role)
      storeRefreshToken(refreshToken)
      setCookies(accessToken, refreshToken)
      return ResponseEntity.ok(
        SuccessRes(statusCode = HttpStatus.OK.value(), content = "Bienvenido ${admin.fullname}")
      )
    }

    val tutor =
      tutorsRepository.findByEmail(req.email)
        ?: throw AccessDeniedException("Correo o contraseña incorrectos")
    tutor.takeIf { bCryptPasswordEncoder.matches(req.password, tutor.password) }
      ?: throw AccessDeniedException("Correo o contraseña incorrectos")
    val id = tutor.id ?: throw IllegalArgumentException("Correo o contraseña incorrectos")
    setCookies(
      jwtService.generateAccessToken(id, tutor.role),
      jwtService.generateRefreshToken(tutor.id, tutor.role),
    )
    return ResponseEntity.ok(
      SuccessRes(statusCode = HttpStatus.OK.value(), content = "Bienvenido ${tutor.fullname}")
    )
  }

  fun studentLogin(req: LoginReq): ResponseEntity<SuccessRes<String>> {
    val student =
      studentsRepository.findByEmail(req.email)
        ?: throw AccessDeniedException("Correo o contraseña incorrectos")
    student.takeIf { bCryptPasswordEncoder.matches(req.password, student.password) }
      ?: throw AccessDeniedException("Correo o contraseña incorrectos")
    val id = student.id ?: throw UnprocessableEntityException("Estudiante no válido")
    val refreshToken = jwtService.generateRefreshToken(id, student.role)
    storeRefreshToken(refreshToken)
    setCookies(jwtService.generateAccessToken(id, student.role), refreshToken)
    return ResponseEntity.ok(
      SuccessRes(statusCode = HttpStatus.OK.value(), content = "Bienvenido ${student.fullname}")
    )
  }

  private fun setCookies(accessToken: String, refreshToken: String) {
    res.addCookie(
      Cookie("accessToken", accessToken).apply {
        isHttpOnly = true
        secure = false
        path = "/"
        maxAge = 120
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

  fun registerStudent(req: StudentRegisterReq): ResponseEntity<SuccessRes<String>> {
    // TODO Save user Picture with Bucket service
    val picture = req.picture.name
    studentsRepository.save(
      StudentsEntity(
        fullname = req.fullName,
        social_id = req.socialId,
        email = req.email,
        password = bCryptPasswordEncoder.encode(req.password),
        picture = picture,
      )
    )
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(
        SuccessRes(
          statusCode = HttpStatus.CREATED.value(),
          content = "${req.fullName} tu cuenta fue creada exitosamente!",
        )
      )
  }

  fun registerTutor(req: TutorRegisterReq): ResponseEntity<SuccessRes<String>> {
    // TODO Save tutor picture with Bucket service
    // TODO Save tutor cv with Bucket service
    // TODO Missing validation when tutor has applied, but hasn't been reviewed
    val picture = req.picture.originalFilename as String
    val cv = req.cv.originalFilename as String
    pendingTutorsRepository.save(
      PendingTutorsEntity(
        socialId = req.socialId,
        fullname = req.fullName,
        picture = picture,
        cv = cv,
        email = req.email,
        password = bCryptPasswordEncoder.encode(req.password),
      )
    )
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(
        SuccessRes(
          statusCode = HttpStatus.CREATED.value(),
          content =
            "${req.fullName}, tu solicitud será procesada por nuestro equipo. Te llegará un correo donde se te informará sobre nuestra decisión",
        )
      )
  }

  fun refreshAccessToken(): ResponseEntity<SuccessRes<String>> {
    val currentRefreshToken = authHelper.refreshToken()
    println("This is the refresh token $currentRefreshToken")
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
        maxAge = 120
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
}

package ptystudybuddy.psb.auth

import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {
  @PostMapping("students/register", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
  fun studentRegister(@Valid @ModelAttribute req: StudentRegisterReq) =
    authService.registerStudent(req)

  @PostMapping("tutors/register", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
  fun tutorRegister(@Valid @ModelAttribute req: TutorRegisterReq) = authService.registerTutor(req)

  @PostMapping("students/login")
  fun studentLogin(@Valid @RequestBody req: LoginReq) = authService.studentLogin(req)

  @PostMapping("login")
  fun adminTutorLogin(@Valid @RequestBody req: LoginReq) = authService.login(req)

  @PostMapping("logout") fun logout() = authService.logout()

  @PostMapping("refresh") fun refreshToken() = authService.refreshAccessToken()

  @PatchMapping("password")
  fun patchPassword(@Valid @RequestBody req: PatchPasswordReq) =
    authService.handlePasswordPatch(req)
}

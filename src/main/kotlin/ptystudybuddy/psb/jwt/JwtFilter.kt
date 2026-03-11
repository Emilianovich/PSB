package ptystudybuddy.psb.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter

@Service
class JwtFilter(private val jwtService: JwtService) : OncePerRequestFilter() {
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain,
  ) {
    val accessToken = request.cookies?.find { it.name == "accessToken" }?.value
    val refreshToken = request.cookies?.find { it.name == "refreshToken" }?.value
    if (refreshToken != null) {
      if (accessToken != null && jwtService.validateAccessToken(accessToken)) {
        val userId = jwtService.getUserIdFromToken(accessToken)
        val role = jwtService.getUserRoleFromToken(accessToken)
        // For DX is important that roles are ALL CAPPED
        val auth =
          UsernamePasswordAuthenticationToken(
            userId,
            null,
            listOf(SimpleGrantedAuthority("ROLE_$role")),
          )
        SecurityContextHolder.getContext().authentication = auth
      }
    }
    filterChain.doFilter(request, response)
  }
}

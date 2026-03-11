package ptystudybuddy.psb.security

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ptystudybuddy.psb.jwt.JwtFilter

@Configuration
class SecurityConfig(private val jwtFilter: JwtFilter) {
  @Bean
  fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
    return httpSecurity
      .csrf { csrf -> csrf.disable() }
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
      .authorizeHttpRequests {
        auth:
          AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
        ->
        auth
          .requestMatchers("/auth/logout")
          .authenticated()
          .requestMatchers("/auth/refresh")
          .authenticated()
          .requestMatchers("/auth/**")
          .permitAll()
          .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD)
          .permitAll()
          .anyRequest()
          .authenticated()
      }
      .exceptionHandling { exceptionConfiguration: ExceptionHandlingConfigurer<HttpSecurity> ->
        exceptionConfiguration.authenticationEntryPoint(
          HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
        )
      }
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
      .build()
  }
}

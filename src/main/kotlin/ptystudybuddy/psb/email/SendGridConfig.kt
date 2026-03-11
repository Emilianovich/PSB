package ptystudybuddy.psb.email

import com.sendgrid.SendGrid
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SendGridConfig(@Value("\${sendgrid.key}") private val key: String) {
  @Bean fun sendGridClient(): SendGrid = SendGrid(key)
}

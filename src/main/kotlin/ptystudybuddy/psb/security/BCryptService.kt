package ptystudybuddy.psb.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class BCryptService {
  private val bCryptPasswordEncoder = BCryptPasswordEncoder()

  fun encode(value: String): String = bCryptPasswordEncoder.encode(value)

  fun matches(value: String, hash: String): Boolean = bCryptPasswordEncoder.matches(value, hash)
}

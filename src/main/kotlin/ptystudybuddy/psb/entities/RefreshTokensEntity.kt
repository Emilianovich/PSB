package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
class RefreshTokensEntity(
  @Id @GeneratedValue(strategy = GenerationType.UUID) val id: String? = null,
  @Column(nullable = false) val token: String,
  @Column(nullable = false, columnDefinition = "TIMESTAMP") val expiry: LocalDateTime,
  @Column(name = "user_id", nullable = false) val userId: String,
  @Column(columnDefinition = "TIMESTAMP", name = "created_at") val createdAt: LocalDateTime? = null,
  @Column(nullable = false) val role: String,
)

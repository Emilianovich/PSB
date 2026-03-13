package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import ptystudybuddy.psb.auth.HasEmailAndPassword

@Entity
@Table(name = "admins")
class AdminsEntity(
  @Id @GeneratedValue(strategy = GenerationType.UUID) val id: String? = null,
  @Column(nullable = false, name = "social_id") val socialId: String,
  @Column(nullable = false) val fullname: String,
  @Column(nullable = false, unique = true) override val email: String,
  @Column(nullable = false) override var password: String,
  @Column(nullable = false) val role: String = "ADMIN",
) : HasEmailAndPassword

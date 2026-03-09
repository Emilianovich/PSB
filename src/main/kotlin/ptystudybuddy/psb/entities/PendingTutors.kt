package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "pending_tutors")
class PendingTutorsEntity(

    @Id
    val social_id: String,

    @Column(nullable=false)
    val fullname: String,

    @Column(nullable=false)
    val picture: String,

    @Column(nullable=false)
    val cv: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column
    val approved: Boolean,

    @Column
    val blacklisted_at: Timestamp



)
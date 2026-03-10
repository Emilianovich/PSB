package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "reviews")
class ReviewsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @Column
    val comment: String,
    @Column(nullable = false)
    val rating: Int,
    @Column(columnDefinition = "TIMESTAMP")
    val date_time: LocalDateTime? = null,
    @ManyToOne
    @JoinColumn(name = "student_id")
    val student_id: StudentsEntity,
    @ManyToOne
    @JoinColumn(name = "tutor_id")
    val tutor_id: TutorsEntity,
    @ManyToOne
    @JoinColumn(name = "session_id")
    val session_id: SessionsEntity,
)

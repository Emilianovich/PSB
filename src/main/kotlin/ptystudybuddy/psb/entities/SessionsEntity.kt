package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "sessions")
class SessionsEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @Column(nullable = false)
    val expected_students: Int,

    @Column
    val available_slots: Int,

    @Column
    val attendance_marked: Boolean,

    @Column(nullable = false)
    val status: String,

    @ManyToOne
    @JoinColumn(name="availability_id", nullable = false)
    val availability_id: AvailabilityEntity,

    @Column(nullable = false, columnDefinition="TIMESTAMP")
    val end_datetime: Timestamp,

    @OneToMany(mappedBy = "session_id")
    val reviews: MutableList<ReviewsEntity> = mutableListOf(),

    @OneToMany(mappedBy = "session_id")
    val inscriptions: MutableList<InscriptionsEntity> = mutableListOf(),

    @OneToMany(mappedBy = "session_id")
    val sessions_assignment: MutableList<SessionAssignmentEntity> = mutableListOf()

)
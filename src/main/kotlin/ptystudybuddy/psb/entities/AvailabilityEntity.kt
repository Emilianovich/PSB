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
import java.time.LocalDate


@Entity
@Table(name="availability")
class AvailabilityEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    val class_id: ClassroomsEntity,

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    val schedule_id: SchedulesEntity,

    @Column(nullable = false)
    val date: LocalDate,

    @Column
    val selected: Boolean,

    @Column(nullable = false, columnDefinition="TIMESTAMP")
    val end_datetime: Timestamp,

    @OneToMany(mappedBy = "availability_id")
    val sessions: MutableList<SessionsEntity> = mutableListOf(),


    )
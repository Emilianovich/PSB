package ptystudybuddy.psb.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.sql.Timestamp


@Entity
@Table(name = "schedules")
class SchedulesEntity(

    @Id
    val id: String? = null,

    @Column(nullable = false, columnDefinition="TIMESTAMP")
    val start_time: Timestamp,

    @Column(nullable = false, columnDefinition="TIMESTAMP")
    val end_time: Timestamp,

    @OneToMany(mappedBy = "schedule_id")
    val availability: MutableList<AvailabilityEntity> = mutableListOf()
)
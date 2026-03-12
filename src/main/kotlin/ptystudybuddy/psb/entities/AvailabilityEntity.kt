package ptystudybuddy.psb.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "availability")
class AvailabilityEntity(
  @Id val id: String? = null,
  @ManyToOne @JoinColumn(name = "class_id", nullable = false) val classId: ClassroomsEntity,
  @ManyToOne @JoinColumn(name = "schedule_id", nullable = false) val scheduleId: SchedulesEntity,
  @Column(nullable = false) val date: LocalDate,
  @Column var selected: Boolean? = null,
  @Column(columnDefinition = "TIMESTAMP", name = "end_datetime")
  val endDatetime: LocalDateTime? = null,
  @OneToMany(mappedBy = "availabilityId")
  @JsonIgnore
  val sessions: MutableList<SessionsEntity> = mutableListOf(),
)

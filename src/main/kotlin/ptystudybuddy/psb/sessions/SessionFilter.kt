package ptystudybuddy.psb.sessions

import jakarta.persistence.criteria.Predicate
import java.time.LocalDate
import org.springframework.data.jpa.domain.Specification
import ptystudybuddy.psb.entities.SessionStatus
import ptystudybuddy.psb.entities.SessionSummaryEntity

object SessionFilter {
  fun getSessions(
    role: String,
    filter: SessionFiltersReq,
    id: String,
  ): Specification<SessionSummaryEntity> {
    return Specification<SessionSummaryEntity> { root, _, builder ->
      val predicates = mutableListOf<Predicate>()
      filter.subjectId?.let { predicates.add(builder.equal(root.get<String>("subjectId"), it)) }
      if (role == "ADMIN") {
        filter.date?.let { predicates.add(builder.equal(root.get<LocalDate>("sessionDate"), it)) }
        filter.tutorId?.let { predicates.add(builder.equal(root.get<String>("tutorId"), it)) }
      }
      // NOTE Making sure tutor cannot get another tutor info
      else {
        predicates.add(builder.equal(root.get<String>("tutorId"), id))
      }
      filter.status?.let {
        predicates.add(builder.equal(root.get<SessionStatus>("sessionStatus"), it))
      }
      builder.and(*predicates.toTypedArray())
    }
  }
}

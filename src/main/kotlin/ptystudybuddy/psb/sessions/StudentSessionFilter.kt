package ptystudybuddy.psb.sessions

import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import ptystudybuddy.psb.entities.StudentSessionsEntity

@Service
class StudentSessionFilter {
  fun getSessions(
    filter: StudentSessionFiltersReq,
    // TODO Maybe change to UUID
    studentId: String,
  ): Specification<StudentSessionsEntity> {
    return Specification<StudentSessionsEntity> { root, _, builder ->
      val predicates = mutableListOf<Predicate>()
      // TODO Remove uppercase() after ENUM Class validation?
      filter.status?.let {
        predicates.add(builder.equal(root.get<String>("sessionStatus"), it.uppercase()))
      }
      // TODO Consider adding a validation for when status is NOT_ACTIVE
      filter.assisted?.let { predicates.add(builder.equal(root.get<Boolean>("assisted"), it)) }
      // Always filter by user making the request
      predicates.add(builder.equal(root.get<String>("studentId"), studentId))
      builder.and(*predicates.toTypedArray())
    }
  }
}

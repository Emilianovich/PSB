package ptystudybuddy.psb.tutors

import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import ptystudybuddy.psb.entities.InscriptionsEntity
import ptystudybuddy.psb.entities.SessionAssignmentEntity
import ptystudybuddy.psb.entities.SessionsEntity
import ptystudybuddy.psb.entities.TutorsEntity

object TutorsSpecification {

  fun fullNameFilter(fullname: String?, orderBy: String): Specification<TutorsEntity> {

    return Specification { root, query, cb ->
      val assignments =
        root.join<TutorsEntity, SessionAssignmentEntity>("sessionsAssignment", JoinType.INNER)

      val sessions =
        assignments.join<SessionAssignmentEntity, SessionsEntity>("sessionId", JoinType.INNER)

      val inscriptions =
        sessions.join<InscriptionsEntity, SessionsEntity>("inscriptions", JoinType.LEFT)

      val filters = mutableListOf<Predicate>()

      fullname?.let { filters.add(cb.like(root.get("fullname"), "%$it%")) }

      filters.add(
        cb.notEqual(sessions.get<Int>("availableSlots"), sessions.get<Int>("expectedStudents"))
      )

      query?.having(cb.notEqual(cb.count(inscriptions.get<String>("sessionId")), 0))
      query?.groupBy(root.get<String>("id"))

      val order =
        if (orderBy.equals("DESC", true)) cb.desc(root.get<Number>("score"))
        else cb.asc(root.get<Number>("score"))

      query?.orderBy(order)

      cb.and(*filters.toTypedArray())
    }
  }
}

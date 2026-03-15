package ptystudybuddy.psb.tutors

import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import ptystudybuddy.psb.entities.TutorsEntity

object TutorsSpecification {

  fun fullNameFilter(fullname: String?, orderBy: String): Specification<TutorsEntity> {

    return Specification { root, query, cb ->
      val filters = mutableListOf<Predicate>()

      fullname?.let { filters.add(cb.like(root.get("fullname"), "%$it%")) }

      query?.groupBy(root.get<String>("id"))

      val order =
        if (orderBy.equals("DESC", true)) cb.desc(root.get<Number>("score"))
        else cb.asc(root.get<Number>("score"))

      query?.orderBy(order)

      cb.and(*filters.toTypedArray())
    }
  }
}

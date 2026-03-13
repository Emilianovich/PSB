package ptystudybuddy.psb.tutors

import org.springframework.data.jpa.domain.Specification
import ptystudybuddy.psb.entities.TutorsEntity

object TutorsSpecification {

  fun fullNameFilter(fullname: String?, orderBy: String): Specification<TutorsEntity> {

    return Specification { root, query, cb ->
      val filter = fullname?.let { cb.like(root.get("fullname"), "%$fullname%") }

      val finalOrder =
        if (orderBy.equals("DESC", ignoreCase = true)) cb.desc(root.get<String>("fullname"))
        else cb.asc(root.get<String>("fullname"))

      query?.orderBy(finalOrder)

      filter
    }
  }
}

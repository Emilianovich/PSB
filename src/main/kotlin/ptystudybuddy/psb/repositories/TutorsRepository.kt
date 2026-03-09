package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.TutorsEntity

interface TutorsRepository : JpaRepository<TutorsEntity, String>

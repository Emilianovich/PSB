package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.SubjectsEntity

interface SubjectsRepository: JpaRepository<SubjectsEntity, String>
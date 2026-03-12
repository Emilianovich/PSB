package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ptystudybuddy.psb.entities.StudentSessionsEntity

interface StudentSessionsRepository :
  JpaRepository<StudentSessionsEntity, Long>, JpaSpecificationExecutor<StudentSessionsEntity>

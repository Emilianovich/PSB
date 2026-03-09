package ptystudybuddy.psb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ptystudybuddy.psb.entities.InscriptionId
import ptystudybuddy.psb.entities.InscriptionsEntity

interface InscriptionsRepository : JpaRepository<InscriptionsEntity, InscriptionId>

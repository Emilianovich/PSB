package ptystudybuddy.psb.inscriptions

import jakarta.validation.constraints.NotBlank

data class CreateInscriptionReq(
  // TODO Change to UUID maybe?
  @field:NotBlank(message = "Información de la sesión no fue enviada") val sessionId: String
)

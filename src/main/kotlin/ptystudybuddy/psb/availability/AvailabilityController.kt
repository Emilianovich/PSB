package ptystudybuddy.psb.availability

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/availability")
class AvailabilityController(val availabilityService: AvailabilityService) {
  @GetMapping fun getAllAvailabilitySessions() = this.availabilityService.getAllAvailabilities()

  @PostMapping
  fun createAvailabilitySession(
    @Valid @RequestBody availabilityData: MutableList<AvailabilityDto>
  ) = this.availabilityService.createAvailability(availabilityData)
}

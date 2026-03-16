package ptystudybuddy.psb.admins

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admins")
class AdminsController(
    private val adminsService: AdminsService
){

    @GetMapping("/profile")
    fun getProfile() = adminsService.getProfile()

}
package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lt.skafis.bankas.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api")
class AuthController (
    private val userService: UserService
) {

    @GetMapping(path = ["/test"])
    @SecurityRequirement(name = "bearerAuth")
    fun test(principal: Principal): String {
        return userService.getUserRole(principal.name) ?: "User not found\uD83D\uDE14"
    }

}
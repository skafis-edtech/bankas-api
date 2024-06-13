package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lt.skafis.bankas.service.UserService
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api")
class TestController (
    private val userService: UserService,
) {

    @GetMapping(path = ["/getUserId"])
    @SecurityRequirement(name = "bearerAuth")
    fun test(principal: Principal): String {
        return principal.name ?: "User not found\uD83D\uDE14"
    }

}
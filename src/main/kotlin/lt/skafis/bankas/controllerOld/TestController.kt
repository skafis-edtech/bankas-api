package lt.skafis.bankas.controllerOld

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lt.skafis.bankas.serviceOld.UserService
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/test")
class TestController (
    private val userService: UserService,
) {

    @GetMapping(path = ["/getUserId"])
    @SecurityRequirement(name = "bearerAuth")
    fun test(principal: Principal): String {
        return principal.name ?: "User not found\uD83D\uDE14"
    }

}
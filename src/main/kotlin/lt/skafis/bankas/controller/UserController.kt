package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lt.skafis.bankas.dto.UserBioDto
import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.security.Principal

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {

    @PatchMapping("/bio")
    @SecurityRequirement(name = "bearerAuth")
    fun updateBio(@RequestBody userBioDto: UserBioDto, principal: Principal): ResponseEntity<Void> {
        userService.updateBio(userBioDto.bio, principal.name)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}

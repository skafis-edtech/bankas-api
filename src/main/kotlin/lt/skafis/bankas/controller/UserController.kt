package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.dto.UserDataDto
import lt.skafis.bankas.dto.UserPublicDataDto
import lt.skafis.bankas.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/user")
@Logged
class UserController {
    @Autowired
    private lateinit var userService: UserService

    @PatchMapping("/bio")
    @Operation(summary = "USER")
    @SecurityRequirement(name = "bearerAuth")
    fun updateBio(
        @RequestBody userDataDto: UserDataDto,
        principal: Principal,
    ): ResponseEntity<Void> {
        userService.updateBio(userDataDto.bio, principal.name)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/publicData/{username}")
    @Operation(summary = "PUBLIC")
    fun getPublicData(
        @PathVariable username: String,
    ): ResponseEntity<UserPublicDataDto> = ResponseEntity.ok(UserPublicDataDto(userService.getBio(username)))

    @GetMapping("/data/{username}")
    @Operation(summary = "USER")
    fun getData(
        @PathVariable username: String,
    ): ResponseEntity<UserDataDto> = ResponseEntity.ok(UserDataDto(userService.getBio(username)))
}

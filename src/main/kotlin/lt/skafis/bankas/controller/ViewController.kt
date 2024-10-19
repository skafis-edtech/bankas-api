package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.config.Logged
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.StatsDto
import lt.skafis.bankas.service.ViewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/view")
@Tag(name = "General View Controller", description = "Stats for public, problemView for USERS")
@SecurityRequirement(name = "bearerAuth")
@Logged
class ViewController {
    @Autowired
    private lateinit var viewService: ViewService

    @GetMapping("/stats")
    fun getStats(): ResponseEntity<StatsDto> = ResponseEntity.ok(StatsDto(viewService.getProblemsCount()))

    @GetMapping("/problem/{skfCode}")
    fun getProblemBySkfCode(
        @PathVariable skfCode: String,
    ): ResponseEntity<ProblemDisplayViewDto> = ResponseEntity.ok(viewService.getProblemBySkfCode(skfCode))
}

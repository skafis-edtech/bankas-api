package lt.skafis.bankas.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.service.RealtimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/misc")
@Tag(name = "Misc Controller", description = "Misc endpoints not for main functionality")
class MiscController {
    @Autowired
    private lateinit var realtimeService: RealtimeService

    @Autowired
    private lateinit var objectMapper: ObjectMapper // Injecting Jackson's ObjectMapper

    @GetMapping("/triggerService")
    @Operation(summary = "This endpoint is called to trigger prod API every 10 minutes and keep it alive")
    fun triggerService(): String = "Service triggered"

    @GetMapping
    fun getAllCategories(): ResponseEntity<List<Category>> = ResponseEntity.ok(realtimeService.getAllCategories())
}

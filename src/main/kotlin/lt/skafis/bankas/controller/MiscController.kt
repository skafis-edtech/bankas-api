package lt.skafis.bankas.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/misc")
@Tag(name = "Misc Controller", description = "Misc endpoints not for main functionality")
class MiscController {
    @Autowired
    private lateinit var objectMapper: ObjectMapper // Injecting Jackson's ObjectMapper

    @GetMapping("/triggerService")
    @Operation(summary = "This endpoint is called to trigger prod API every 10 minutes and keep it alive")
    fun triggerService(): String = "Service triggered"
}

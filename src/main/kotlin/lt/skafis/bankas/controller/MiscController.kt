package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/misc")
@Tag(name = "Misc Controller", description = "Misc endpoints not for main functionality")
class MiscController {
    @GetMapping("/triggerService")
    @Operation(summary = "This endpoint is called to trigger prod API every 10 minutes and keep it alive")
    fun triggerService(): String = "Service triggered"
}

package lt.skafis.bankas.controller
import lt.skafis.bankas.dto.RecaptchaResponse
import lt.skafis.bankas.service.RecaptchaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val recaptchaService: RecaptchaService) {

    @PostMapping("/validateRecaptcha")
    fun validateRecaptcha(@RequestParam("token") token: String): ResponseEntity<RecaptchaResponse> =
        ResponseEntity.ok(recaptchaService.validateRecaptcha(token))
}

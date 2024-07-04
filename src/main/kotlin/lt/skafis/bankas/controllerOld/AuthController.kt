package lt.skafis.bankas.controllerOld
import lt.skafis.bankas.dtoOld.RecaptchaResponse
import lt.skafis.bankas.serviceOld.RecaptchaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val recaptchaService: RecaptchaService) {

    @PostMapping("/validateRecaptcha")
    fun validateRecaptcha(@RequestParam("token") token: String): ResponseEntity<RecaptchaResponse> =
        ResponseEntity.ok(recaptchaService.validateRecaptcha(token))
}

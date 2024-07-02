package lt.skafis.bankas.service

import lt.skafis.bankas.dto.RecaptchaRequest
import lt.skafis.bankas.dto.RecaptchaResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class RecaptchaServiceImpl(
    @Value("\${recaptcha.site.key}") private val recaptchaSiteKey: String,
    @Value("\${recaptcha.api.key}") private val recaptchaApiKey: String
): RecaptchaService {
    private val recaptchaVerifyUrl = "https://recaptchaenterprise.googleapis.com/v1/projects/bankas-skafis/assessments?key=$recaptchaApiKey"

    override fun validateRecaptcha(response: String): RecaptchaResponse? {
        val restTemplate = RestTemplate()
        val recaptchaResponse = restTemplate.postForObject(recaptchaVerifyUrl, RecaptchaRequest(
            token = response,
            sitekey = recaptchaSiteKey
        ), RecaptchaResponse::class.java)
        return recaptchaResponse
    }
}
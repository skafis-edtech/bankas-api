package lt.skafis.bankas.serviceOld

import lt.skafis.bankas.dtoOld.RecaptchaResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class RecaptchaServiceImpl(
    @Value("\${recaptcha.secret.key}") private val recaptchaSecretKey: String
): RecaptchaService {
    private val recaptchaVerifyUrl = "https://www.google.com/recaptcha/api/siteverify?secret=$recaptchaSecretKey&response="

    override fun validateRecaptcha(response: String): RecaptchaResponse? {
        val restTemplate = RestTemplate()
        val url = recaptchaVerifyUrl + response
        val recaptchaResponse = restTemplate.postForObject(url, null, RecaptchaResponse::class.java)
        return recaptchaResponse
    }
}
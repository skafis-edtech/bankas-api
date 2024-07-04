package lt.skafis.bankas.service

import lt.skafis.bankas.dto.RecaptchaResponse

interface RecaptchaService {
    fun validateRecaptcha(response: String): RecaptchaResponse?
}
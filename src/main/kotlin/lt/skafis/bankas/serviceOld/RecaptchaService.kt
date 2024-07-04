package lt.skafis.bankas.serviceOld

import lt.skafis.bankas.dtoOld.RecaptchaResponse

interface RecaptchaService {
    fun validateRecaptcha(response: String): RecaptchaResponse?
}
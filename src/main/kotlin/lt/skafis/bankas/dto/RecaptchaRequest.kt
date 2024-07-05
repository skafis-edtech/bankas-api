package lt.skafis.bankas.dto

data class RecaptchaRequest(
    val token: String,
    val action: String? = null,
    val sitekey: String
)

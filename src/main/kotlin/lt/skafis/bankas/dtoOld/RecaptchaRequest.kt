package lt.skafis.bankas.dtoOld

data class RecaptchaRequest(
    val token: String,
    val action: String? = null,
    val sitekey: String
)

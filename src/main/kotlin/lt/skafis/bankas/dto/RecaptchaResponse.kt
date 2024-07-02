package lt.skafis.bankas.dto

data class RecaptchaResponse(
    val success: Boolean,
    val challenge_ts: String?,
    val hostname: String?,
    val errorCodes: List<String>?
)

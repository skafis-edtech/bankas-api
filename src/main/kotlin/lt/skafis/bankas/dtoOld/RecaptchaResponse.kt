package lt.skafis.bankas.dtoOld

data class RecaptchaResponse(
    val success: Boolean,
    val challenge_ts: String?,
    val hostname: String?,
    val errorCodes: List<String>?
)

package lt.skafis.bankas.auth

data class User (
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val role: String = ""
)
package lt.skafis.bankas.modelOld

data class User (
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val role: Role = Role.USER,
    val bio: String = ""
)
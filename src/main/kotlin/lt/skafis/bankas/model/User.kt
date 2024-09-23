package lt.skafis.bankas.model

data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val role: Role = Role.USER,
    var bio: String = "",
    val balance: Int = 0,
)

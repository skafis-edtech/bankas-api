package lt.skafis.bankas.model

import jakarta.validation.constraints.Email

data class User (

    @Email
    val email: String = "",
    val username: String = "",
    val role: Role = Role.USER,
    val bio: String = ""
)
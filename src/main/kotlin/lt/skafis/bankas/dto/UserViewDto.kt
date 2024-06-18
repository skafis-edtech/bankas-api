package lt.skafis.bankas.dto

import jakarta.validation.constraints.Email
import lt.skafis.bankas.model.Role

data class UserViewDto (
    val id: String = "",
    @Email
    val email: String = "",
    val username: String = "",
    val role: Role = Role.USER,
    val bio: String = ""
)
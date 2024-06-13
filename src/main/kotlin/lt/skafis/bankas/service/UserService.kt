package lt.skafis.bankas.service

import lt.skafis.bankas.dto.UserViewDto

interface UserService {
    fun getUserById(userId: String): UserViewDto?
    fun getUsernameById(userId: String): String?
    fun getRoleById(userId: String): String?
}

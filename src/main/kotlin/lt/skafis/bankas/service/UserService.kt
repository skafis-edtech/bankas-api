package lt.skafis.bankas.service

import lt.skafis.bankas.dto.UserViewDto
import lt.skafis.bankas.model.Role

interface UserService {
    fun getUserById(userId: String): UserViewDto?
    fun getUsernameById(userId: String): String?
    fun getRoleById(userId: String): Role?
    fun updateBio(bio: String, userId: String): Boolean
}

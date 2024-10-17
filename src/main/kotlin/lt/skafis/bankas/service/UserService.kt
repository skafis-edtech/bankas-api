package lt.skafis.bankas.service

import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.User

interface UserService {
    fun getUserById(userId: String): User

    fun getUsernameById(userId: String): String

    fun getUserIdByUsername(username: String): String

    fun getRoleById(userId: String): Role

    fun updateBio(
        bio: String,
        userId: String,
    ): Boolean

    fun getBio(username: String): String

    fun grantRoleAtLeast(role: Role)

    fun getCurrentUserId(): String

    fun getCurrentUserUsername(): String
}

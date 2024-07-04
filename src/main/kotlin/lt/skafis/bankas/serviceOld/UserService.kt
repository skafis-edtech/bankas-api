package lt.skafis.bankas.serviceOld

import lt.skafis.bankas.modelOld.Role
import lt.skafis.bankas.modelOld.User

interface UserService {
    fun getUserById(userId: String): User
    fun getUsernameById(userId: String): String
    fun getRoleById(userId: String): Role
    fun updateBio(bio: String, userId: String): Boolean
    fun getBio(username: String): String
}

package lt.skafis.bankas.service

interface UserService {
    fun getUserRole(userId: String): String?
}

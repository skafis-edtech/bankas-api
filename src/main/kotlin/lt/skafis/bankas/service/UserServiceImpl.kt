package lt.skafis.bankas.service

import lt.skafis.bankas.repository.FirestoreUserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: FirestoreUserRepository) : UserService {
    override fun getUserRole(userId: String): String? {
        val userDto = userRepository.getUserById(userId)
        return userDto?.role
    }
}

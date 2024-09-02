package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.User
import lt.skafis.bankas.repository.firestore.UserRepository
import lt.skafis.bankas.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {
    override fun getUserById(userId: String): User {
        val user = userRepository.getUserById(userId) ?: throw NotFoundException("User not found")
        return user
    }

    override fun getUsernameById(userId: String): String {
        val user = userRepository.getUserById(userId) ?: throw NotFoundException("User not found")
        return user.username
    }

    override fun getUserIdByUsername(username: String): String {
        val user = userRepository.getByUsername(username) ?: throw NotFoundException("User not found")
        return user.id
    }

    override fun getRoleById(userId: String): Role {
        val user = userRepository.getUserById(userId) ?: throw NotFoundException("User not found")
        return user.role
    }

    override fun updateBio(
        bio: String,
        userId: String,
    ): Boolean {
        val success = userRepository.updateUserBio(userId, bio)
        if (!success) {
            throw NotFoundException("User not found")
        }
        return true
    }

    override fun getBio(username: String): String {
        val user = userRepository.getByUsername(username) ?: throw NotFoundException("User not found")
        return user.bio
    }

    override fun grantRoleAtLeast(role: Role) {
        val user = userRepository.getUserById(getCurrentUserId()) ?: throw NotFoundException("User not found")
        if (!(
                role === Role.USER &&
                    (user.role === Role.USER || user.role === Role.ADMIN || user.role === Role.SUPER_ADMIN) ||
                    role === Role.ADMIN &&
                    (user.role === Role.ADMIN || user.role === Role.SUPER_ADMIN) ||
                    role === Role.SUPER_ADMIN &&
                    user.role === Role.SUPER_ADMIN
            )
        ) {
            throw IllegalArgumentException("Unauthorized access")
        }
    }

    override fun getCurrentUserId(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication?.name ?: throw IllegalStateException("No authenticated user found")
    }

    override fun getCurrentUserUsername(): String {
        val userId = getCurrentUserId()
        return getUsernameById(userId)
    }
}

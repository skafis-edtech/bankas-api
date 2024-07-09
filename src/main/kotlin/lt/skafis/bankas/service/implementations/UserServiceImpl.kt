package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.model.Role
import lt.skafis.bankas.modelOld.User
import lt.skafis.bankas.repository.UserRepository
import lt.skafis.bankas.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    val log: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun getUserById(userId: String): User {
        log.info("Getting user by id: $userId")
        val user = userRepository.getUserById(userId) ?: throw NotFoundException("User not found")
        log.info("User found")
        return user
    }

    override fun getUsernameById(userId: String): String {
        log.info("Getting username by id: $userId")
        val user = userRepository.getUserById(userId) ?: throw NotFoundException("User not found")
        log.info("Username found")
        return user.username
    }

    override fun getRoleById(userId: String): Role {
        log.info("Getting role by id: $userId")
        val user = userRepository.getUserById(userId) ?: throw NotFoundException("User not found")
        log.info("Role found")
        return user.role
    }

    override fun updateBio(bio: String, userId: String): Boolean {
        log.info("Updating bio for user: $userId")
        val success = userRepository.updateUserBio(userId, bio)
        if (!success) {
            throw NotFoundException("User not found")
        }
        log.info("Bio updated successfully")
        return true
    }

    override fun getBio(username: String): String {
        log.info("Getting bio for user: $username")
        val user = userRepository.getByUsername(username) ?: throw NotFoundException("User not found")
        log.info("Bio found")
        return user.bio
    }

    override fun grantRoleAtLeast(role: Role) {
        val user = userRepository.getUserById(getCurrentUserId()) ?: throw NotFoundException("User not found")
        if (!(role === Role.USER && (user.role === Role.USER || user.role === Role.ADMIN || user.role === Role.SUPER_ADMIN) ||
                role === Role.ADMIN && (user.role === Role.ADMIN || user.role === Role.SUPER_ADMIN) ||
                role === Role.SUPER_ADMIN && user.role === Role.SUPER_ADMIN)) {
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

package lt.skafis.bankas.config

import lt.skafis.bankas.model.User
import lt.skafis.bankas.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(userId: String): UserDetails {
        // Named as username, but actually is user ID
        val user = userRepository.getUserById(userId)
            ?: throw UsernameNotFoundException("User not found with id: $userId")

        val authorities = getAuthorities(user)
        authorities.forEach { authority -> println("Assigned authority: ${authority.authority}") }

        return org.springframework.security.core.userdetails.User(
            user.username,
            "",
            authorities
        )
    }

    private fun getAuthorities(user: User): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
    }
}

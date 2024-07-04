package lt.skafis.bankas.config

import lt.skafis.bankas.modelOld.User
import lt.skafis.bankas.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.getUserByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")

        return org.springframework.security.core.userdetails.User(
            user.username, "", getAuthorities(user)
        )
    }

    private fun getAuthorities(user: User): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(user.role.toString()))
    }
}

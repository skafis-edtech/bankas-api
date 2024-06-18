package lt.skafis.bankas.service

import lt.skafis.bankas.dto.UserViewDto
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.repository.FirestoreUserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.webjars.NotFoundException

class UserServiceTest {

    private lateinit var userRepository: FirestoreUserRepository
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = Mockito.mock(FirestoreUserRepository::class.java)
        userService = UserServiceImpl(userRepository)
    }

    @Test
    fun `getUserById returns user when user exists`() {
        val userId = "testUserId"
        val user = UserViewDto(userId, "test@test.com", "testUsername", Role.USER)
        `when`(userRepository.getUserById(userId)).thenReturn(user)

        val result = userService.getUserById(userId)

        assertEquals(UserViewDto(userId, user.email, user.username, user.role), result)
    }

    @Test
    fun `getUserById throws NotFoundException when user does not exist`() {
        val userId = "testUserId"
        `when`(userRepository.getUserById(userId)).thenReturn(null)

        assertThrows(NotFoundException::class.java) {
            userService.getUserById(userId)
        }
    }

    @Test
    fun `getUsernameById returns username when user exists`() {
        val userId = "testUserId"
        val user = UserViewDto(userId, "test@test.com", "testUsername", Role.USER)
        `when`(userRepository.getUserById(userId)).thenReturn(user)

        val result = userService.getUsernameById(userId)

        assertEquals(user.username, result)
    }

    @Test
    fun `getUsernameById throws NotFoundException when user does not exist`() {
        val userId = "testUserId"
        `when`(userRepository.getUserById(userId)).thenReturn(null)

        assertThrows(NotFoundException::class.java) {
            userService.getUsernameById(userId)
        }
    }

    @Test
    fun `getRoleById returns role when user exists`() {
        val userId = "testUserId"
        val user = UserViewDto(userId, "test@test.com", "testUsername", Role.USER)
        `when`(userRepository.getUserById(userId)).thenReturn(user)

        val result = userService.getRoleById(userId)

        assertEquals(user.role, result)
    }

    @Test
    fun `getRoleById throws NotFoundException when user does not exist`() {
        val userId = "testUserId"
        `when`(userRepository.getUserById(userId)).thenReturn(null)

        assertThrows(NotFoundException::class.java) {
            userService.getRoleById(userId)
        }
    }

    @Test
    fun `updateBio returns true when update is successful`() {
        val userId = "testUserId"
        val bio = "testBio"
        `when`(userRepository.updateUserBio(userId, bio)).thenReturn(true)

        val result = userService.updateBio(bio, userId)

        assertEquals(true, result)
    }

    @Test
    fun `updateBio throws NotFoundException when update is not successful`() {
        val userId = "testUserId"
        val bio = "testBio"
        `when`(userRepository.updateUserBio(userId, bio)).thenReturn(false)

        assertThrows(NotFoundException::class.java) {
            userService.updateBio(bio, userId)
        }
    }
}
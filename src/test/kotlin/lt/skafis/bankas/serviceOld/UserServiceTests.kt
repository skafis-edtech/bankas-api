package lt.skafis.bankas.serviceOld

import lt.skafis.bankas.modelOld.Role
import lt.skafis.bankas.modelOld.User
import lt.skafis.bankas.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.webjars.NotFoundException

class UserServiceTests {

    private lateinit var userRepository: UserRepository
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = Mockito.mock(UserRepository::class.java)
        userService = UserServiceImpl(userRepository)
    }

    @Test
    fun `getUserById returns user when user exists`() {
        val userId = "testUserId"
        val user = User(userId, "test@test.com", "testUsername", Role.USER)
        `when`(userRepository.getUserById(userId)).thenReturn(user)

        val result = userService.getUserById(userId)

        assertEquals(User(userId, user.email, user.username, user.role), result)
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
        val user = User(userId, "test@test.com", "testUsername", Role.USER)
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
        val user = User(userId, "test@test.com", "testUsername", Role.USER)
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

    @Test
    fun `getBio returns bio when user exists`() {
        val username = "testUsername"
        val user = User("testUserId", "test@test.com", username,  Role.USER,"testBio")
        `when`(userRepository.getByUsername(username)).thenReturn(user)

        val result = userService.getBio(username)

        assertEquals(user.bio, result)
    }

    @Test
    fun `getBio throws NotFoundException when user does not exist`() {
        val username = "testUsername"
        `when`(userRepository.getByUsername(username)).thenReturn(null)

        assertThrows(NotFoundException::class.java) {
            userService.getBio(username)
        }
    }
}
package lt.skafis.bankas.service

import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.UnderReviewCategory
import lt.skafis.bankas.repository.FirestoreCategoryRepository
import lt.skafis.bankas.repository.FirestoreUnderReviewCategoryRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class CategoryServiceTests {

    private lateinit var categoryService: CategoryService

    private lateinit var firestoreCategoryRepository: FirestoreCategoryRepository
    private lateinit var userService: UserService
    private lateinit var firestoreUnderReviewCategoryRepository: FirestoreUnderReviewCategoryRepository

    @BeforeEach
    fun setUp() {
        userService = Mockito.mock(UserService::class.java)
        firestoreCategoryRepository = Mockito.mock(FirestoreCategoryRepository::class.java)
        firestoreUnderReviewCategoryRepository = Mockito.mock(FirestoreUnderReviewCategoryRepository::class.java)
        categoryService = CategoryServiceImpl(firestoreCategoryRepository, userService, firestoreUnderReviewCategoryRepository)
    }

    @Test
    fun `getAllUnderReviewCategories returns categories when user is admin`() {
        val userId = "testUserId"
        `when`(userService.getRoleById(userId)).thenReturn(Role.ADMIN)

        val result = categoryService.getAllUnderReviewCategories(userId)

        assertEquals(listOf<UnderReviewCategory>(), result)
    }

    @Test
    fun `getAllUnderReviewCategories throws IllegalStateException when user is not admin`() {
        val userId = "testUserId"
        `when`(userService.getRoleById(userId)).thenReturn(Role.USER)

        assertThrows(IllegalStateException::class.java) {
            categoryService.getAllUnderReviewCategories(userId)
        }
    }
}
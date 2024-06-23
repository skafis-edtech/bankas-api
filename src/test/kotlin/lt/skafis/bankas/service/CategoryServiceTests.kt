package lt.skafis.bankas.service
//
//import org.webjars.NotFoundException
//import lt.skafis.bankas.model.Category
//import lt.skafis.bankas.model.ReviewStatus
//import lt.skafis.bankas.model.Role
//import lt.skafis.bankas.model.UnderReviewCategory
//import lt.skafis.bankas.repository.FirestoreCategoryRepository
//import lt.skafis.bankas.repository.FirestoreUnderReviewCategoryRepository
//import lt.skafis.bankas.repository.FirestoreUnderReviewProblemRepository
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito
//import org.mockito.Mockito.*
//
//class CategoryServiceTests {
//
//    private lateinit var categoryService: CategoryService
//
//    private lateinit var firestoreCategoryRepository: FirestoreCategoryRepository
//    private lateinit var userService: UserService
//    private lateinit var firestoreUnderReviewCategoryRepository: FirestoreUnderReviewCategoryRepository
//    private lateinit var firestoreUnderReviewProblemRepository: FirestoreUnderReviewProblemRepository
//
//    @BeforeEach
//    fun setUp() {
//        userService = Mockito.mock(UserService::class.java)
//        firestoreCategoryRepository = Mockito.mock(FirestoreCategoryRepository::class.java)
//        firestoreUnderReviewCategoryRepository = Mockito.mock(FirestoreUnderReviewCategoryRepository::class.java)
//        categoryService = CategoryServiceImpl(firestoreCategoryRepository, userService, firestoreUnderReviewCategoryRepository, firestoreUnderReviewProblemRepository)
//    }
//
//    @Test
//    fun `getAllUnderReviewCategories returns categories when user is admin`() {
//        val userId = "testUserId"
//        `when`(userService.getRoleById(userId)).thenReturn(Role.ADMIN)
//
//        val result = categoryService.getAllUnderReviewCategories(userId)
//
//        assertEquals(listOf<UnderReviewCategory>(), result)
//    }
//
//    @Test
//    fun `getAllUnderReviewCategories throws IllegalStateException when user is not admin`() {
//        val userId = "testUserId"
//        `when`(userService.getRoleById(userId)).thenReturn(Role.USER)
//
//        assertThrows(IllegalStateException::class.java) {
//            categoryService.getAllUnderReviewCategories(userId)
//        }
//    }
//
//    @Test
//    fun `approveCategory returns Category when user is admin and category exists`() {
//        val userId = "testUserId"
//        val categoryId = "testCategoryId"
//        `when`(userService.getRoleById(userId)).thenReturn(Role.ADMIN)
//        `when`(userService.getUsernameById(userId)).thenReturn("testUsername")
//        `when`(firestoreUnderReviewCategoryRepository.getCategoryById(categoryId)).thenReturn(UnderReviewCategory("testCategoryId", "testName", "testDescription", "testAuthor", "testCreatedOn", "testLastModifiedOn", ReviewStatus.PENDING))
//        `when`(firestoreUnderReviewCategoryRepository.deleteCategory(categoryId)).thenReturn(true)
//
//        val result = categoryService.approveCategory(categoryId, userId)
//
//        assertEquals(Category::class.java, result::class.java)
//    }
//
//    @Test
//    fun `approveCategory throws IllegalStateException when user is not admin`() {
//        val userId = "testUserId"
//        val categoryId = "testCategoryId"
//        `when`(userService.getRoleById(userId)).thenReturn(Role.USER)
//
//        assertThrows(IllegalStateException::class.java) {
//            categoryService.approveCategory(categoryId, userId)
//        }
//    }
//
//    @Test
//    fun `approveCategory throws NotFoundException when category does not exist`() {
//        val userId = "testUserId"
//        val categoryId = "testCategoryId"
//        `when`(userService.getRoleById(userId)).thenReturn(Role.ADMIN)
//        `when`(userService.getUsernameById(userId)).thenReturn("testUsername")
//
//        assertThrows(NotFoundException::class.java) {
//            categoryService.approveCategory(categoryId, userId)
//        }
//    }
//}
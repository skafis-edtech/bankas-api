package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.config.AppConfig
import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.ReviewStatus
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.Visibility
import lt.skafis.bankas.repository.firestore.CategoryRepository
import lt.skafis.bankas.repository.firestore.ProblemRepository
import lt.skafis.bankas.repository.firestore.SourceRepository
import lt.skafis.bankas.service.CategoryService
import lt.skafis.bankas.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class CategoryServiceImpl : CategoryService {
    @Autowired
    private lateinit var problemRepository: ProblemRepository

    @Autowired
    private lateinit var sourceRepository: SourceRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var appConfig: AppConfig

    override fun createCategory(categoryPostDto: CategoryPostDto): Category {
        if (categoryPostDto.visibility == Visibility.PUBLIC) {
            userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        }
        val userId = if (categoryPostDto.visibility == Visibility.PRIVATE) userService.getCurrentUserId() else ""
        val category =
            categoryRepository.create(
                Category(
                    name = categoryPostDto.name,
                    description = categoryPostDto.description,
                    visibility = categoryPostDto.visibility,
                    ownerOfPrivateId = userId,
                ),
            )
        return category
    }

    override fun updateCategory(
        id: String,
        categoryPostDto: CategoryPostDto,
    ): Category {
        var categoryToUpdate = categoryRepository.findById(id) ?: throw NotFoundException("Category with id $id not found")
        val userId = userService.getCurrentUserId()
        if (categoryToUpdate.visibility == Visibility.PRIVATE && categoryToUpdate.ownerOfPrivateId != userId) {
            throw Exception("User with id $userId is not the owner of category with id $id")
        }
        if (categoryToUpdate.visibility == Visibility.PUBLIC || categoryPostDto.visibility == Visibility.PUBLIC) {
            userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        }

        categoryToUpdate =
            categoryToUpdate.copy(
                name = categoryPostDto.name,
                description = categoryPostDto.description,
                visibility = categoryPostDto.visibility,
            )
        val success = categoryRepository.update(categoryToUpdate, id)
        return if (success) {
            categoryToUpdate
        } else {
            throw Exception("Failed to update category with id $id")
        }
    }

    override fun deleteCategory(id: String) {
        if (id == appConfig.unsortedCategoryId) {
            throw Exception("Cannot delete default category")
        }
        val category = categoryRepository.findById(id) ?: throw NotFoundException("Category with id $id not found")
        val userId = userService.getCurrentUserId()
        if (category.visibility == Visibility.PRIVATE) {
            if (category.ownerOfPrivateId != userId) {
                throw Exception("User with id $userId is not the owner of category with id $id")
            }
        } else {
            userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        }
        val success = categoryRepository.delete(id)
        if (!success) throw Exception("Failed to delete category with id $id")

        removeCategoryIdFromProblems(id, userId)
    }

    override fun sortProblem(
        problemId: String,
        categoryIdList: List<String>,
    ) {
        val userId = userService.getCurrentUserId()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem with id $problemId not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            if (source.reviewStatus == ReviewStatus.APPROVED) {
                userService.grantRoleAtLeast(Role.ADMIN)
            } else {
                throw IllegalAccessException("Unauthorized access")
            }
        }
        val success =
            problemRepository.update(
                problem.copy(
                    categories = categoryIdList,
                ),
                problemId,
            )
        if (!success) throw Exception("Failed to update problem with id $problemId")
    }

    fun removeCategoryIdFromProblems(
        categoryId: String,
        userId: String,
    ) {
        val problems = problemRepository.getAllAvailableByCategory(categoryId, userId)
        problems.forEach {
            val updatedCategories = it.categories.toMutableList()
            updatedCategories.remove(categoryId)
            problemRepository.update(
                it.copy(
                    categories = updatedCategories,
                ),
                it.id,
            )
        }
    }
}

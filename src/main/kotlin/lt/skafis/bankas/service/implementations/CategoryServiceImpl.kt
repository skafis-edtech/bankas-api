package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.Visibility
import lt.skafis.bankas.repository.firestore.CategoryRepository
import lt.skafis.bankas.service.CategoryService
import lt.skafis.bankas.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class CategoryServiceImpl : CategoryService {
    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var userService: UserService

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
        val newIsPrivate: Visibility
        if (categoryToUpdate.visibility == Visibility.PRIVATE) {
            val userId = userService.getCurrentUserId()
            if (categoryToUpdate.ownerOfPrivateId != userId) {
                userService.grantRoleAtLeast(Role.SUPER_ADMIN)
                newIsPrivate = categoryPostDto.visibility
            } else {
                newIsPrivate = Visibility.PRIVATE
            }
        } else {
            userService.grantRoleAtLeast(Role.SUPER_ADMIN)
            newIsPrivate = categoryPostDto.visibility
        }
        categoryToUpdate =
            categoryToUpdate.copy(
                name = categoryPostDto.name,
                description = categoryPostDto.description,
                visibility = newIsPrivate,
            )
        val success = categoryRepository.update(categoryToUpdate, id)
        return if (success) {
            categoryToUpdate
        } else {
            throw Exception("Failed to update category with id $id")
        }
    }

    override fun deleteCategory(id: String) {
        val category = categoryRepository.findById(id) ?: throw NotFoundException("Category with id $id not found")
        if (category.visibility == Visibility.PRIVATE) {
            val userId = userService.getCurrentUserId()
            if (category.ownerOfPrivateId != userId) {
                throw Exception("User with id $userId is not the owner of category with id $id")
            }
        } else {
            userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        }
        val success = categoryRepository.delete(id)
        if (!success) throw Exception("Failed to delete category with id $id")
    }

    override fun sortProblem(
        problemId: String,
        categoryIdList: List<String>,
    ) {
        TODO("Not yet implemented")
    }
}

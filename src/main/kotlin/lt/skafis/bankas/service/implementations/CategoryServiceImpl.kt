package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Role
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
        if (categoryPostDto.isPrivate) {
            userService.grantRoleAtLeast(Role.USER)
        } else {
            userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        }
        val userId = if (categoryPostDto.isPrivate) userService.getCurrentUserId() else ""
        val category =
            categoryRepository.create(
                Category(
                    name = categoryPostDto.name,
                    description = categoryPostDto.description,
                    isPrivate = categoryPostDto.isPrivate,
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
        val newIsPrivate: Boolean
        if (categoryToUpdate.isPrivate) {
            val userId = userService.getCurrentUserId()
            if (categoryToUpdate.ownerOfPrivateId != userId) {
                userService.grantRoleAtLeast(Role.SUPER_ADMIN)
                newIsPrivate = categoryPostDto.isPrivate
            } else {
                newIsPrivate = true
            }
        } else {
            userService.grantRoleAtLeast(Role.SUPER_ADMIN)
            newIsPrivate = categoryPostDto.isPrivate
        }
        categoryToUpdate =
            categoryToUpdate.copy(
                name = categoryPostDto.name,
                description = categoryPostDto.description,
                isPrivate = newIsPrivate,
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
        if (category.isPrivate) {
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
}

package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.repository.CategoryRepository
import lt.skafis.bankas.service.CategoryService
import lt.skafis.bankas.service.UserService
import lt.skafis.bankas.modelOld.Role
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val userService: UserService
): CategoryService {

    override fun getCategories(): List<Category> {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        return categoryRepository.findAll()
    }

    override fun getCategoryById(id: String): Category {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        return categoryRepository.findById(id) ?: throw NotFoundException("Category with id $id not found")
    }

    override fun createCategory(categoryPostDto: CategoryPostDto): Category {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        return categoryRepository.create(
            Category(name = categoryPostDto.name, description = categoryPostDto.description)
        )
    }

    override fun updateCategory(id: String, categoryPostDto: CategoryPostDto): Category {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        var categoryToUpdate = categoryRepository.findById(id) ?: throw NotFoundException("Category with id $id not found")
        categoryToUpdate = categoryToUpdate.copy(
            name = categoryPostDto.name,
            description = categoryPostDto.description
        )
        val success = categoryRepository.update(categoryToUpdate, id)
        return if (success) categoryToUpdate
        else throw Exception("Failed to update category with id $id")
    }

    override fun deleteCategory(id: String) {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        val success = categoryRepository.delete(id)
        if (!success) throw Exception("Failed to delete category with id $id")
    }
}
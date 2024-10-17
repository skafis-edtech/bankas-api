package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.repository.firestore.CategoryRepository
import lt.skafis.bankas.service.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class CategoryServiceImpl : CategoryService {
    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    override fun createCategory(categoryPostDto: CategoryPostDto): Category {
        val category =
            categoryRepository.create(
                Category(name = categoryPostDto.name, description = categoryPostDto.description),
            )
        return category
    }

    override fun updateCategory(
        id: String,
        categoryPostDto: CategoryPostDto,
    ): Category {
        var categoryToUpdate = categoryRepository.findById(id) ?: throw NotFoundException("Category with id $id not found")
        categoryToUpdate =
            categoryToUpdate.copy(
                name = categoryPostDto.name,
                description = categoryPostDto.description,
            )
        val success = categoryRepository.update(categoryToUpdate, id)
        return if (success) {
            categoryToUpdate
        } else {
            throw Exception("Failed to update category with id $id")
        }
    }

    override fun deleteCategory(id: String) {
        val success = categoryRepository.delete(id)
        if (!success) throw Exception("Failed to delete category with id $id")
    }

    override fun createPrivateCategory(categoryPostDto: CategoryPostDto): Category {
        TODO("Not yet implemented")
    }

    override fun updatePrivateCategory(
        id: String,
        categoryPostDto: CategoryPostDto,
    ): Category {
        TODO("Not yet implemented")
    }

    override fun deletePrivateCategory(id: String) {
        TODO("Not yet implemented")
    }
}

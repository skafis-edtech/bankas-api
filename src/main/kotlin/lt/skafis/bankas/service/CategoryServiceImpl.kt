package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.dto.CategoryViewDto
import org.springframework.stereotype.Service

@Service
class CategoryServiceImpl : CategoryService {
    override fun getCategoryById(id: String): CategoryViewDto {
        TODO("Not yet implemented")
    }

    override fun createCategory(category: CategoryPostDto): CategoryViewDto {
        TODO("Not yet implemented")
    }

    override fun updateCategory(id: String, category: CategoryPostDto): CategoryViewDto {
        TODO("Not yet implemented")
    }

    override fun deleteCategoryWithProblems(id: String): String {
        TODO("Not yet implemented")
    }

    override fun getAllCategories(): List<CategoryViewDto> {
        TODO("Not yet implemented")
    }
}
package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.dto.CategoryViewDto
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.repository.FirestoreCategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryServiceImpl(
    private val firestoreCategoryRepository: FirestoreCategoryRepository
) : CategoryService {
    override fun getCategoryById(id: String): CategoryViewDto {
        TODO("Not yet implemented")
    }

    override fun createCategory(category: CategoryPostDto): CategoryViewDto {
        TODO("Not yet implemented")
    }

    override fun updateCategory(id: String, category: CategoryPostDto): CategoryViewDto {
        TODO("Not yet implemented")
    }

    override fun deleteCategoryWithProblems(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAllCategories(): List<CategoryViewDto> {
        TODO("Not yet implemented")
    }

    override fun getCategoriesCount(): CountDto {
        return CountDto(firestoreCategoryRepository.countDocuments())
    }
}
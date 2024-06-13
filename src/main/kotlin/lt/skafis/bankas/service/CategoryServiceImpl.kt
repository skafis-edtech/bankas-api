package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.dto.CategoryViewDto
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.repository.FirestoreCategoryRepository
import org.apache.logging.log4j.util.InternalException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.webjars.NotFoundException
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date

@Service
class CategoryServiceImpl(
    private val firestoreCategoryRepository: FirestoreCategoryRepository,
    private val userService: UserService,
    private val problemService: ProblemService
) : CategoryService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getCategoryById(id: String): CategoryViewDto {
        log.info("Getting category by id: $id")
        val category = firestoreCategoryRepository.getCategoryById(id) ?: throw NotFoundException("Category not found")
        log.info("Category found")
        return CategoryViewDto(id, category.name, category.description, category.createdOn, category.createdBy)
    }

    override fun createCategory(category: CategoryPostDto, userId: String): CategoryViewDto {
        val username = userService.getUsernameById(userId) ?: throw NotFoundException("User not found")
        log.info("Creating category by user: $username")
        val categoryToCreate = CategoryViewDto("", category.name, category.description, DateTimeFormatter.ISO_INSTANT.format(Instant.now()), username)
        val id = firestoreCategoryRepository.createCategory(categoryToCreate)
        log.info("Category created successfully")
        return categoryToCreate.copy(id = id)
    }

    override fun updateCategory(id: String, category: CategoryPostDto, userId: String): CategoryViewDto {
        val username = userService.getUsernameById(userId) ?: throw NotFoundException("User not found")
        log.info("Updating category $id by user: $username")
        val categoryToUpdate = firestoreCategoryRepository.getCategoryById(id) ?: throw NotFoundException("Category not found")
        if (categoryToUpdate.createdBy != username) throw InternalException("User is not the creator of the category")
        val updatedCategory = categoryToUpdate.copy(id = id, name = category.name, description = category.description)
        val success = firestoreCategoryRepository.updateCategory(updatedCategory)
        if (!success) throw InternalException("Failed to update category")
        log.info("Category updated successfully")
        return updatedCategory
    }

    override fun deleteCategoryWithProblems(id: String, userId: String): Boolean {
        val username = userService.getUsernameById(userId) ?: throw NotFoundException("User not found")
        log.info("Deleting category $id with all problems by user: $username")
        val categoryToDelete = firestoreCategoryRepository.getCategoryById(id) ?: throw NotFoundException("Category not found")
        if (categoryToDelete.createdBy != username) throw InternalException("User is not the creator of the category")

        val problems = problemService.getProblemsByCategoryId(id)
        problems.forEach { problemService.deleteProblem(it.id, userId) }

        val success = firestoreCategoryRepository.deleteCategory(id)
        if (!success) throw InternalException("Failed to delete category")
        log.info("Category with problems deleted successfully")
        return true
    }

    override fun getAllCategories(): List<CategoryViewDto> {
        log.info("Fetching all categories")
        val categories = firestoreCategoryRepository.getAllCategories()
        if (categories.isEmpty()) throw NotFoundException("No categories found")
        log.info("Categories fetched successfully")
        return categories
    }

    override fun getCategoriesCount(): CountDto {
        log.info("Fetching categories count")
        val count = firestoreCategoryRepository.countDocuments()
        if (count == 0L) throw InternalException("Failed to count categories")
        log.info("Categories count fetched successfully")
        return CountDto(count)
    }
}
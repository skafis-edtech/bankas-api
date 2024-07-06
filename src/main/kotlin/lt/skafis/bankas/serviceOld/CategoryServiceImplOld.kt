package lt.skafis.bankas.serviceOld

import lt.skafis.bankas.dto.CategoryPostDto
import lt.skafis.bankas.dtoOld.CountDto
import lt.skafis.bankas.model.ReviewStatus
import lt.skafis.bankas.modelOld.*
import lt.skafis.bankas.repositoryOld.FirestoreCategoryRepository
import lt.skafis.bankas.repositoryOld.FirestoreUnderReviewCategoryRepository
import lt.skafis.bankas.repositoryOld.FirestoreUnderReviewProblemRepository
import lt.skafis.bankas.service.UserService
import org.apache.logging.log4j.util.InternalException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.webjars.NotFoundException
import java.time.Instant
import java.time.format.DateTimeFormatter

@Service
class CategoryServiceImplOld(
    private val firestoreCategoryRepository: FirestoreCategoryRepository,
    private val userService: UserService,
    private val firestoreUnderReviewCategoryRepository: FirestoreUnderReviewCategoryRepository,
    private val firestoreUnderReviewProblemRepository: FirestoreUnderReviewProblemRepository
) : CategoryService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getPublicCategoryById(id: String): Category {
        log.info("Getting category by id: $id")
        val category = firestoreCategoryRepository.getCategoryById(id) ?: throw NotFoundException("Category not found")
        log.info("Category found")
        return category
    }

    override fun getAllPublicCategories(): List<Category> {
        log.info("Fetching all categories")
        val categories = firestoreCategoryRepository.getAllCategories()
        if (categories.isEmpty()) throw NotFoundException("No categories found")
        log.info("Categories fetched successfully")
        return categories
    }

    override fun getPublicCategoriesCount(): CountDto {
        log.info("Fetching categories count")
        val count = firestoreCategoryRepository.countDocuments()
        if (count == 0L) throw InternalException("Failed to count categories")
        log.info("Categories count fetched successfully")
        return CountDto(count)
    }

    override fun submitCategory(category: CategoryPostDto, userId: String): UnderReviewCategory {
        val username = userService.getUsernameById(userId)
        log.info("Submitting category by user: $username")
        val categoryToSubmit = UnderReviewCategory(
            name = category.name,
            description = category.description,
            author = username
        )
        val id = firestoreUnderReviewCategoryRepository.createCategory(categoryToSubmit)
        log.info("Category submitted successfully")
        return categoryToSubmit.copy(id = id)
    }

    override fun getAllUnderReviewCategories(userId: String): List<UnderReviewCategory> {
        val role = userService.getRoleById(userId)
        if (role != Role.ADMIN) throw IllegalStateException("User is not an admin")
        val username = userService.getUsernameById(userId)

        log.info("Fetching all under review categories by user: $username")
        val categories = firestoreUnderReviewCategoryRepository.getAllCategories()
        log.info("Under review categories fetched successfully")
        return categories
    }

    override fun approveCategory(id: String, userId: String): Category {
        val role = userService.getRoleById(userId)
        if (role != Role.ADMIN) throw IllegalStateException("User is not an admin")
        val username = userService.getUsernameById(userId)

        log.info("Approving category $id by user: $username")
        val categoryToApprove = firestoreUnderReviewCategoryRepository.getCategoryById(id) ?: throw NotFoundException("Category not found")
        val newCategory = Category(
            id = id,
            name = categoryToApprove.name,
            description = categoryToApprove.description,
            author = categoryToApprove.author,
            approvedBy = username,
            createdOn = categoryToApprove.createdOn,
            lastModifiedOn = categoryToApprove.lastModifiedOn
        )
        firestoreCategoryRepository.createCategoryWithSpecifiedId(newCategory)
        log.info("Category created in main collection")
        val success = firestoreUnderReviewCategoryRepository.deleteCategory(id)
        if (!success) throw InternalException("Failed to delete category from under review collection")
        log.info("Category deleted from under review collection")
        log.info("Category approved successfully")
        return newCategory
    }

    override fun getAllMySubmittedCategories(userId: String): List<UnderReviewCategory> {
        val username = userService.getUsernameById(userId)
        log.info("Fetching all under review categories submitted by user: $username")
        val categories = firestoreUnderReviewCategoryRepository.getCategoriesByAuthor(username)
        log.info("Fetched successfully")
        return categories
    }

    override fun getAllMyApprovedCategories(userId: String): List<Category> {
        val username = userService.getUsernameById(userId)
        log.info("Fetching all approved categories by user: $username")
        val categories = firestoreCategoryRepository.getCategoriesByAuthor(username)
        log.info("Fetched successfully")
        return categories
    }

    override fun rejectCategory(id: String, rejectMsg: String, userId: String): UnderReviewCategory {
        val role = userService.getRoleById(userId)
        if (role != Role.ADMIN) throw IllegalStateException("User is not an admin")
        val username = userService.getUsernameById(userId)

        log.info("Rejecting category $id by user: $username")
        val categoryToReject = firestoreUnderReviewCategoryRepository.getCategoryById(id) ?: throw NotFoundException("Category not found")
        val newCategory = categoryToReject.copy(
            reviewStatus = ReviewStatus.REJECTED,
            rejectedOn = DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
            rejectedBy = username,
            rejectionMessage = rejectMsg
        )
        val success = firestoreUnderReviewCategoryRepository.updateCategory(newCategory)
        if (!success) throw InternalException("Failed to update category")
        log.info("Category rejected successfully")
        return newCategory
    }

    override fun updateMyUnderReviewCategory(id: String, category: CategoryPostDto, userId: String): UnderReviewCategory {
        val username = userService.getUsernameById(userId)
        log.info("Updating under review category $id by user: $username")
        val categoryToUpdate = firestoreUnderReviewCategoryRepository.getCategoryById(id) ?: throw NotFoundException("Category not found")
        if (categoryToUpdate.author != username) throw IllegalStateException("User is not the author of the category")

        val newCategory = categoryToUpdate.copy(
            name = category.name,
            description = category.description,
            lastModifiedOn = DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
            reviewStatus = ReviewStatus.PENDING
        )
        val success = firestoreUnderReviewCategoryRepository.updateCategory(newCategory)
        if (!success) throw InternalException("Failed to update category")
        log.info("Category updated successfully")
        return newCategory
    }

    override fun deleteUnderReviewCategoryWithUnderReviewProblems(id: String, userId: String): Boolean {
        val username = userService.getUsernameById(userId)
        log.info("Deleting under review category $id by user: $username")
        val categoryToDelete = firestoreUnderReviewCategoryRepository.getCategoryById(id) ?: throw NotFoundException("Category not found")
        if (categoryToDelete.author != username) throw IllegalStateException("User is not the author of the category")

        val success = firestoreUnderReviewCategoryRepository.deleteCategory(id)
        if (!success) throw InternalException("Failed to delete category")
        log.info("Category deleted successfully")

        val problemsInQuestion = firestoreUnderReviewProblemRepository.getProblemsByCategoryId(id)
        problemsInQuestion.forEach {
            if (it.author == username) {
                val successProblem = firestoreUnderReviewProblemRepository.deleteProblem(it.id)
                if (!successProblem) throw InternalException("Failed to delete problem")
            } else {
                val newProblem = it.copy(
                    reviewStatus = ReviewStatus.REJECTED,
                    rejectedOn = Instant.now().toString(),
                    rejectedBy = username,
                    rejectionMessage = "Category was deleted by the category author. Please reassign a new category."
                )
                val successProblem = firestoreUnderReviewProblemRepository.updateProblem(newProblem)
                if (!successProblem) throw InternalException("Failed to update problem")
            }
        }

        return true
    }
}
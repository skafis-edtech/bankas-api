package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.CategoryDisplayDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Visibility
import lt.skafis.bankas.repository.firestore.CategoryRepository
import lt.skafis.bankas.repository.firestore.ProblemRepository
import lt.skafis.bankas.service.CategoryViewService
import lt.skafis.bankas.service.StorageService
import lt.skafis.bankas.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CategoryViewServiceImpl : CategoryViewService {
    @Autowired
    private lateinit var storageService: StorageService

    @Autowired
    private lateinit var problemRepository: ProblemRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var userService: UserService

    override fun getProblemsByCategoryShuffle(
        categoryId: String,
        page: Int,
        size: Int,
    ): List<ProblemDisplayViewDto> {
        val userId = userService.getCurrentUserId()
        problemRepository
            .getByCategoryId(categoryId)
            .filter {
                it.isApproved
            }.map {
                ProblemDisplayViewDto(
                    id = it.id,
                    sourceListNr = it.sourceListNr,
                    skfCode = it.skfCode,
                    problemText = it.problemText,
                    problemImageSrc = storageService.utilsGetImageSrc(it.problemImagePath),
                    answerText = it.answerText,
                    answerImageSrc = storageService.utilsGetImageSrc(it.answerImagePath),
                    categories = it.categories,
                    sourceId = it.sourceId,
                )
            }.shuffled()
    }

    override fun getCategoryById(categoryId: String): Category {
        val userId = userService.getCurrentUserId()
        val category = categoryRepository.findById(categoryId) ?: throw Exception("Category with id $categoryId not found")
        if (category.visibility == Visibility.PRIVATE && category.ownerOfPrivateId != userId) {
            throw Exception("Category with id $categoryId not found")
        } else {
            return category
        }
    }

    override fun getCategories(
        page: Int,
        size: Int,
        search: String,
    ): List<CategoryDisplayDto> {
        val userId = userService.getCurrentUserId()
        return categoryRepository
            .getSearchPageableCategories(search, size, (page * size).toLong(), userId)
            .sortedBy {
                it.name
            }.map {
                val count = problemRepository.countApprovedByCategoryId(it.id)
                it.toDisplayDto(count.toInt())
            }
    }
}

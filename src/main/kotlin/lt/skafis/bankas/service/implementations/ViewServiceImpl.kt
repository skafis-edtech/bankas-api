package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.CategoryDisplayDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.*
import lt.skafis.bankas.repository.firestore.CategoryRepository
import lt.skafis.bankas.repository.firestore.ProblemRepository
import lt.skafis.bankas.repository.firestore.SourceRepository
import lt.skafis.bankas.service.StorageService
import lt.skafis.bankas.service.UserService
import lt.skafis.bankas.service.ViewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class ViewServiceImpl : ViewService {
    @Autowired
    private lateinit var sourceRepository: SourceRepository

    @Autowired
    private lateinit var storageService: StorageService

    @Autowired
    private lateinit var problemRepository: ProblemRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var userService: UserService

    override fun getProblemsCount(): Long = problemRepository.countApproved()

    override fun getCategoryProblemCount(categoryId: String): Long = problemRepository.countApprovedByCategoryId(categoryId)

    override fun getProblemsByCategoryShuffle(categoryId: String): List<ProblemDisplayViewDto> =
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

    override fun getCategoryById(categoryId: String): Category =
        categoryRepository.findById(categoryId) ?: throw Exception("Category with id $categoryId not found")

    override fun getCategories(
        page: Int,
        size: Int,
        search: String,
    ): List<CategoryDisplayDto> =
        categoryRepository
            .getSearchPageableCategories(search, size, (page * size).toLong())
            .sortedBy {
                it.name
            }.map {
                val count = problemRepository.countApprovedByCategoryId(it.id)
                it.toDisplayDto(count.toInt())
            }

    override fun getProblemBySkfCode(skfCode: String): ProblemDisplayViewDto {
        val problem = problemRepository.getBySkfCode(skfCode)
        if (problem == Problem()) {
            return ProblemDisplayViewDto(problemVisibility = ProblemVisibility.NOT_EXISTING)
        } else {
            val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
            val userId = userService.getCurrentUserId() // TODO: FOR SOME DUMB FUCKIN REASON THIS DOESN"T GET USER'S ID!!!
            if (problem.isApproved ||
                source.authorId == userId ||
                userService.getUserById(userId).role == Role.ADMIN
            ) {
                return ProblemDisplayViewDto(
                    id = problem.id,
                    sourceListNr = problem.sourceListNr,
                    skfCode = problem.skfCode,
                    problemText = problem.problemText,
                    problemImageSrc = storageService.utilsGetImageSrc(problem.problemImagePath),
                    answerText = problem.answerText,
                    answerImageSrc = storageService.utilsGetImageSrc(problem.answerImagePath),
                    categories = problem.categories,
                    sourceId = problem.sourceId,
                    problemVisibility = ProblemVisibility.VISIBLE,
                )
            } else {
                return ProblemDisplayViewDto(problemVisibility = ProblemVisibility.HIDDEN)
            }
        }
    }

    override fun getSourceById(sourceId: String): SourceDisplayDto {
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source with id $sourceId not found")
        if (source.authorId != userService.getCurrentUserId() && source.reviewStatus != ReviewStatus.APPROVED) {
            userService.grantRoleAtLeast(Role.ADMIN)
        }
        val authorUsername = userService.getUsernameById(source.authorId)
        val count = problemRepository.countBySource(sourceId)
        return source.toDisplayDto(authorUsername, count.toInt())
    }

    override fun getSourcesByAuthor(
        authorUsername: String,
        page: Int,
        size: Int,
        search: String,
    ): List<SourceDisplayDto> {
        val authorId = userService.getUserIdByUsername(authorUsername)
        return sourceRepository
            .getByAuthorSearchPageable(authorId, search, size, (page * size).toLong(), isApproved = true)
            .map {
                val count = problemRepository.countBySource(it.id)
                it.toDisplayDto(authorUsername, count.toInt())
            }
    }

    override fun getUnsortedProblems(): List<ProblemDisplayViewDto> =
        problemRepository
            .getUnsortedApprovedProblems()
            .map {
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

    override fun getUnsortedProblemsCount(): Long = problemRepository.countUnsortedApproved()

    override fun getApprovedSources(
        page: Int,
        size: Int,
        search: String,
    ): List<SourceDisplayDto> =
        sourceRepository
            .getApprovedSearchPageable(
                search,
                size,
                (page * size).toLong(),
            ).map {
                val count = problemRepository.countBySource(it.id)
                val authorUsername = userService.getUsernameById(it.authorId)
                it.toDisplayDto(authorUsername, count.toInt())
            }

    override fun getProblemsBySource(
        sourceId: String,
        page: Int,
        size: Int,
    ): List<ProblemDisplayViewDto> {
        val userId = userService.getCurrentUserId()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId && source.reviewStatus != ReviewStatus.APPROVED) {
            userService.grantRoleAtLeast(Role.ADMIN)
        }

        return problemRepository
            .getBySourceIdPageable(sourceId, size, (page * size).toLong())
            .map {
                ProblemDisplayViewDto(
                    id = it.id,
                    sourceListNr = it.sourceListNr,
                    skfCode = it.skfCode,
                    problemText = it.problemText,
                    problemImageSrc = storageService.utilsGetImageSrc(it.problemImagePath),
                    answerText = it.answerText,
                    answerImageSrc = storageService.utilsGetImageSrc(it.answerImagePath),
                    sourceId = it.sourceId,
                    categories = it.categories,
                )
            }
    }
}

package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.*
import lt.skafis.bankas.repository.firestore.CategoryRepository
import lt.skafis.bankas.repository.firestore.ProblemRepository
import lt.skafis.bankas.repository.firestore.SourceRepository
import lt.skafis.bankas.service.ProblemService
import lt.skafis.bankas.service.PublicService
import lt.skafis.bankas.service.SourceService
import lt.skafis.bankas.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PublicServiceImpl : PublicService {
    @Autowired
    private lateinit var sourceRepository: SourceRepository

    @Autowired
    private lateinit var problemService: ProblemService

    @Autowired
    private lateinit var problemRepository: ProblemRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var sourceService: SourceService

    @Autowired
    private lateinit var userService: UserService

    override fun getProblemsCount(): Long = problemRepository.countApproved()

    override fun getCategoriesCount(): Long = categoryRepository.countDocuments()

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
                    problemImageSrc = problemService.utilsGetImageSrc(it.problemImagePath),
                    answerText = it.answerText,
                    answerImageSrc = problemService.utilsGetImageSrc(it.answerImagePath),
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
    ): List<Category> =
        categoryRepository
            .getSearchPageableCategories(search, size, (page * size).toLong())
            .sortedBy {
                it.name
            }

    override fun getProblemBySkfCode(skfCode: String): ProblemDisplayViewDto {
        val problem = problemRepository.getBySkfCode(skfCode)
        if (!problem.isApproved) throw Exception("Problem with skfCode $skfCode is not approved")

        return ProblemDisplayViewDto(
            id = problem.id,
            sourceListNr = problem.sourceListNr,
            skfCode = problem.skfCode,
            problemText = problem.problemText,
            problemImageSrc = problemService.utilsGetImageSrc(problem.problemImagePath),
            answerText = problem.answerText,
            answerImageSrc = problemService.utilsGetImageSrc(problem.answerImagePath),
            categories = problem.categories,
            sourceId = problem.sourceId,
        )
    }

    override fun getSourceById(sourceId: String): SourceDisplayDto {
        val source = sourceService.getSourceById(sourceId)
        if (source.authorId != userService.getCurrentUserId()) {
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
                    problemImageSrc = problemService.utilsGetImageSrc(it.problemImagePath),
                    answerText = it.answerText,
                    answerImageSrc = problemService.utilsGetImageSrc(it.answerImagePath),
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
}

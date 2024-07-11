package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Source
import lt.skafis.bankas.repository.CategoryRepository
import lt.skafis.bankas.repository.ProblemRepository
import lt.skafis.bankas.repository.SourceRepository
import lt.skafis.bankas.service.ProblemService
import lt.skafis.bankas.service.PublicService
import lt.skafis.bankas.service.SourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PublicServiceImpl: PublicService {

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

    override fun getProblemsCount(): Long {
        return problemRepository.countDocuments()
    }

    override fun getCategoriesCount(): Long {
        return categoryRepository.countDocuments()
    }

    override fun getProblemsByCategory(categoryId: String): List<ProblemDisplayViewDto> {
        return problemRepository.getByCategoryId(categoryId)
            .map {
                ProblemDisplayViewDto(
                    id = it.id,
                    problemText = it.problemText,
                    problemImageSrc = problemService.utilsGetImageSrc(it.problemImagePath),
                    answerText = it.answerText,
                    answerImageSrc = problemService.utilsGetImageSrc(it.answerImagePath),
                )
            }
    }

    override fun getCategoryById(categoryId: String): Category {
        return categoryRepository.findById(categoryId) ?: throw Exception("Category with id $categoryId not found")
    }

    override fun getCategories(): List<Category> {
        return categoryRepository.findAll()
    }

    override fun getProblemById(problemId: String): ProblemDisplayViewDto {
        val problem = problemRepository.findById(problemId) ?: throw Exception("Problem with id $problemId not found")
        return ProblemDisplayViewDto(
            id = problem.id,
            problemText = problem.problemText,
            problemImageSrc = problemService.utilsGetImageSrc(problem.problemImagePath),
            answerText = problem.answerText,
            answerImageSrc = problemService.utilsGetImageSrc(problem.answerImagePath),
        )
    }

    override fun getSourceById(sourceId: String): Source {
        return sourceService.getSourceById(sourceId)
    }

    override fun getSourcesByAuthor(authorUsername: String): List<Source> {
        return sourceRepository.getByAuthor(authorUsername)
    }

}
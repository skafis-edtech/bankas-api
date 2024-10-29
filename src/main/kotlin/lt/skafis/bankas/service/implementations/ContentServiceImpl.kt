package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.config.AppConfig
import lt.skafis.bankas.dto.*
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.model.ReviewStatus
import lt.skafis.bankas.model.SortBy
import lt.skafis.bankas.model.Source
import lt.skafis.bankas.repository.firestore.ProblemRepository
import lt.skafis.bankas.repository.firestore.SourceRepository
import lt.skafis.bankas.repository.storage.StorageRepository
import lt.skafis.bankas.service.ContentService
import lt.skafis.bankas.service.ProblemMetaService
import lt.skafis.bankas.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.threeten.bp.Instant
import org.webjars.NotFoundException
import java.util.*

@Service
class ContentServiceImpl(
    private val appConfig: AppConfig,
) : ContentService {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var sourceRepository: SourceRepository

    @Autowired
    private lateinit var problemRepository: ProblemRepository

    @Autowired
    private lateinit var storageRepository: StorageRepository

    @Autowired
    private lateinit var metaService: ProblemMetaService

    override fun submitSourceData(sourceData: SourceSubmitDto): String {
        val userId = userService.getCurrentUserId()

        val createdSource =
            sourceRepository.create(
                Source(
                    name = sourceData.name,
                    description = sourceData.description,
                    visibility = sourceData.visibility,
                    authorId = userId,
                ),
            )
        return createdSource.id
    }

    override fun submitProblem(
        sourceId: String,
        problem: ProblemSubmitDto,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?,
    ): IdSkfDto {
        val userId = userService.getCurrentUserId()
        val imagesUUID = UUID.randomUUID()
        var problemImagePath = ""
        var answerImagePath = ""
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source $sourceId")
        }
        if (problem.sourceListNr < 1) {
            throw IllegalArgumentException("Source list number must be greater than 0")
        }

        problemImageFile?.let {
            problemImagePath = "problems/${imagesUUID}_${it.originalFilename}"
            storageRepository.uploadImage(it, problemImagePath)
        }
        answerImageFile?.let {
            answerImagePath = "answers/${imagesUUID}_${it.originalFilename}"
            storageRepository.uploadImage(it, answerImagePath)
        }

        val skfCode = metaService.getLowestUnusedSkfCode()
        metaService.amendUsedSkfCodeList(skfCode)

        val createdProblem =
            problemRepository.create(
                Problem(
                    skfCode = skfCode,
                    sourceListNr = problem.sourceListNr,
                    problemText = problem.problemText,
                    problemImagePath = problemImagePath,
                    answerText = problem.answerText,
                    answerImagePath = answerImagePath,
                    sourceId = sourceId,
                    categories = listOf(appConfig.unsortedCategoryId),
                ),
            )

        val modifiedSource =
            source.copy(
                lastModifiedOn = Instant.now().toString(),
                reviewStatus = ReviewStatus.PENDING,
            )
        sourceRepository.update(modifiedSource, sourceId)

        if (source.reviewStatus == ReviewStatus.APPROVED) {
            val sourceProblems = problemRepository.getBySourceId(sourceId)
            sourceProblems.forEach {
                problemRepository.update(it.copy(isApproved = false), it.id)
                if (it.isApproved) {
                    metaService.removeSkfCodeFromUsedList(it.skfCode)
                }
            }
        }
        return IdSkfDto(createdProblem.id, createdProblem.skfCode)
    }

    override fun getMySources(
        page: Int,
        size: Int,
        search: String,
        sortBy: SortBy,
    ): List<SourceDisplayDto> {
        val userId = userService.getCurrentUserId()
        return sourceRepository
            .getByAuthorSearchPageable(
                userId,
                search,
                size,
                (page * size).toLong(),
                sortBy = sortBy,
            ).map {
                val count = problemRepository.countBySource(it.id)
                it.toDisplayDto(userService.getUsernameById(it.authorId), count.toInt())
            }
    }

    override fun deleteSource(sourceId: String) {
        val userId = userService.getCurrentUserId()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source $sourceId")
        }
        val problems = problemRepository.getBySourceId(sourceId)
        if (problems.isNotEmpty()) {
            throw IllegalAccessException("Source $sourceId has problems, delete them first")
        }
        sourceRepository.delete(sourceId)
    }

    override fun deleteProblem(problemId: String) {
        val userId = userService.getCurrentUserId()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source ${problem.sourceId}")
        }
        problemRepository.delete(problemId)
        if (problem.skfCode.isNotEmpty()) {
            metaService.removeSkfCodeFromUsedList(problem.skfCode)
        }
        if (problem.problemImagePath.startsWith("problems/")) {
            storageRepository.deleteImage(problem.problemImagePath)
        }
        if (problem.answerImagePath.startsWith("answers/")) {
            storageRepository.deleteImage(problem.answerImagePath)
        }
        unapproveSource(source)
    }

    override fun updateSource(
        sourceId: String,
        sourceData: SourceSubmitDto,
    ): SourceDisplayDto {
        val userId = userService.getCurrentUserId()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source $sourceId")
        }
        val updatedSource =
            source.copy(
                name = sourceData.name,
                description = sourceData.description,
                visibility = sourceData.visibility,
                lastModifiedOn = Instant.now().toString(),
                reviewStatus = ReviewStatus.PENDING,
            )
        unapproveSource(updatedSource)
        return updatedSource.toDisplayDto(userService.getUsernameById(updatedSource.authorId))
    }

    override fun updateProblemTexts(
        problemId: String,
        problemTextsDto: ProblemTextsDto,
    ) {
        val userId = userService.getCurrentUserId()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source ${problem.sourceId}")
        }
        val updatedProblem =
            problem.copy(
                sourceListNr = problem.sourceListNr,
                problemText = problemTextsDto.problemText,
                answerText = problemTextsDto.answerText,
            )
        problemRepository.update(updatedProblem, problemId)

        unapproveSource(source)
    }

    override fun deleteProblemImage(problemId: String) {
        val userId = userService.getCurrentUserId()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source ${problem.sourceId}")
        }
        if (problem.problemImagePath.startsWith("problems/")) {
            storageRepository.deleteImage(problem.problemImagePath)
        } else {
            throw IllegalAccessException("Problem $problemId does not have a problem image")
        }
        val updatedProblem =
            problem.copy(
                problemImagePath = "",
            )
        problemRepository.update(updatedProblem, problemId)
        unapproveSource(source)
    }

    override fun deleteAnswerImage(problemId: String) {
        val userId = userService.getCurrentUserId()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source ${problem.sourceId}")
        }
        if (problem.answerImagePath.startsWith("answers/")) {
            storageRepository.deleteImage(problem.answerImagePath)
        } else {
            throw IllegalAccessException("Problem $problemId does not have an answer image")
        }
        val updatedProblem =
            problem.copy(
                answerImagePath = "",
            )
        problemRepository.update(updatedProblem, problemId)
        unapproveSource(source)
    }

    override fun uploadProblemImage(
        problemId: String,
        image: MultipartFile,
    ): String {
        val userId = userService.getCurrentUserId()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source ${problem.sourceId}")
        }
        val imagesUUID = UUID.randomUUID()
        val problemImagePath = "problems/${imagesUUID}_${image.originalFilename}"
        storageRepository.uploadImage(image, problemImagePath)
        val updatedProblem =
            problem.copy(
                problemImagePath = problemImagePath,
            )
        problemRepository.update(updatedProblem, problemId)
        unapproveSource(source)
        return problemImagePath
    }

    override fun uploadAnswerImage(
        problemId: String,
        image: MultipartFile,
    ): String {
        val userId = userService.getCurrentUserId()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source ${problem.sourceId}")
        }
        val imagesUUID = UUID.randomUUID()
        val answerImagePath = "answers/${imagesUUID}_${image.originalFilename}"
        storageRepository.uploadImage(image, answerImagePath)
        val updatedProblem =
            problem.copy(
                answerImagePath = answerImagePath,
            )
        problemRepository.update(updatedProblem, problemId)
        unapproveSource(source)
        return answerImagePath
    }

    private fun unapproveSource(source: Source) {
        val modifiedSource =
            source.copy(
                lastModifiedOn = Instant.now().toString(),
                reviewStatus = ReviewStatus.PENDING,
            )
        sourceRepository.update(modifiedSource, source.id)
        val problems = problemRepository.getBySourceId(source.id)
        problems.forEach {
            problemRepository.update(it.copy(isApproved = false), it.id)
        }
    }
}

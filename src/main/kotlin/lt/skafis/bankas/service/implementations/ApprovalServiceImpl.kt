package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.*
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.model.ReviewStatus
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.Source
import lt.skafis.bankas.repository.ProblemRepository
import lt.skafis.bankas.repository.SourceRepository
import lt.skafis.bankas.repository.StorageRepository
import lt.skafis.bankas.service.ApprovalService
import lt.skafis.bankas.service.ProblemMetaService
import lt.skafis.bankas.service.ProblemService
import lt.skafis.bankas.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.webjars.NotFoundException
import java.util.*

@Service
class ApprovalServiceImpl: ApprovalService {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var sourceRepository: SourceRepository

    @Autowired
    private lateinit var problemRepository: ProblemRepository

    @Autowired
    private lateinit var problemService: ProblemService

    @Autowired
    private lateinit var storageRepository: StorageRepository

    @Autowired
    private lateinit var metaService: ProblemMetaService

    override fun submitSourceData(sourceData: SourceSubmitDto): String {
        val userId = userService.getCurrentUserId()

        val createdSource = sourceRepository.create(
            Source(
                name = sourceData.name,
                description = sourceData.description,
                authorId = userId
            )
        )
        return createdSource.id
    }

    override fun submitProblem(
        sourceId: String,
        problem: ProblemSubmitDto,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?
    ): String {
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

        val createdProblem = problemRepository.create(
            Problem(
                sourceListNr = problem.sourceListNr,
                problemText = problem.problemText,
                problemImagePath = problemImagePath,
                answerText = problem.answerText,
                answerImagePath = answerImagePath,
                sourceId = sourceId
            )
        )

        val modifiedSource = source.copy(
            lastModifiedOn = Instant.now().toString(),
            reviewStatus = ReviewStatus.PENDING
        )
        sourceRepository.update(modifiedSource, sourceId)

        if (source.reviewStatus == ReviewStatus.APPROVED) {
            val sourceProblems = problemRepository.getBySourceId(sourceId)
            sourceProblems.forEach {
                problemRepository.update(it.copy(skfCode = "", isApproved = false), it.id)
                if (it.isApproved) {
                    metaService.removeSkfCodeFromUsedList(it.skfCode)
                }
            }
        }
        return createdProblem.id
    }

    override fun getMySources(): List<SourceDisplayDto> {
        val userId = userService.getCurrentUserId()
        return sourceRepository.getByAuthor(userId)
            .sortedByDescending {
                it.lastModifiedOn
            }
            .map {
                it.toDisplayDto(userService.getUsernameById(it.authorId))
            }
    }

    override fun getProblemsBySource(sourceId: String): List<ProblemDisplayViewDto> {
        val userId = userService.getCurrentUserId()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId  && source.reviewStatus != ReviewStatus.APPROVED) {
            userService.grantRoleAtLeast(Role.ADMIN)
        }

        return problemRepository.getBySourceId(sourceId)
            .sortedBy {
                it.sourceListNr
            }
            .map {
                ProblemDisplayViewDto(
                    id = it.id,
                    sourceListNr = it.sourceListNr,
                    skfCode = it.skfCode,
                    problemText = it.problemText,
                    problemImageSrc = problemService.utilsGetImageSrc(it.problemImagePath),
                    answerText = it.answerText,
                    answerImageSrc = problemService.utilsGetImageSrc(it.answerImagePath),
                    sourceId = it.sourceId,
                    categories = it.categories
                )
            }
    }

    override fun approve(sourceId: String, reviewMessage: String): SourceDisplayDto {
        val username = userService.getCurrentUserUsername()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        val updatedSource = source.copy(
            reviewStatus = ReviewStatus.APPROVED,
            reviewHistory = "${source.reviewHistory}${Instant.now()} $username rašė: $reviewMessage\n",
        )
        sourceRepository.update(updatedSource, sourceId)

        if (source.reviewStatus != ReviewStatus.APPROVED) {
            val problems = problemRepository.getBySourceId(sourceId)
            problems.forEach {
                if (it.skfCode.isEmpty()) {
                    val skfCode = metaService.getLowestUnusedSkfCode()
                    metaService.amendUsedSkfCodeList(skfCode)
                    val updatedProblem = it.copy(
                        skfCode = skfCode,
                        isApproved = true
                    )
                    problemRepository.update(updatedProblem, it.id)
                } else {
                    val updatedProblem = it.copy(
                        isApproved = true
                    )
                    problemRepository.update(updatedProblem, it.id)
                }
            }
        }

        return updatedSource.toDisplayDto(userService.getUsernameById(updatedSource.authorId))
    }

    override fun reject(sourceId: String, reviewMessage: String): SourceDisplayDto {
        val username = userService.getCurrentUserUsername()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        val updatedSource = source.copy(
            reviewStatus = ReviewStatus.REJECTED,
            reviewHistory = "${source.reviewHistory}${Instant.now()} $username rašė: $reviewMessage\n",
        )
        sourceRepository.update(updatedSource, sourceId)

        if (source.reviewStatus == ReviewStatus.APPROVED) {
            val problems = problemRepository.getBySourceId(sourceId)
            problems.forEach {
                val updatedProblem = it.copy(
                    isApproved = false
                )
                problemRepository.update(updatedProblem, it.id)
            }
        }
        return updatedSource.toDisplayDto(userService.getUsernameById(updatedSource.authorId))
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
        if (problem.problemImagePath.startsWith("problems/")) {
            storageRepository.deleteImage(problem.problemImagePath)
        }
        if (problem.answerImagePath.startsWith("answers/")) {
            storageRepository.deleteImage(problem.answerImagePath)
        }
        unapproveSource(source)
    }

    override fun updateSource(sourceId: String, sourceData: SourceSubmitDto): SourceDisplayDto {
        val userId = userService.getCurrentUserId()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source $sourceId")
        }
        val updatedSource = source.copy(
            name = sourceData.name,
            description = sourceData.description,
            lastModifiedOn = Instant.now().toString(),
            reviewStatus = ReviewStatus.PENDING
        )
        unapproveSource(updatedSource)
        return updatedSource.toDisplayDto(userService.getUsernameById(updatedSource.authorId))
    }

    override fun getSources(): List<SourceDisplayDto> {
        return sourceRepository.findAll()
            .sortedByDescending {
                it.lastModifiedOn
            }
            .map {
                it.toDisplayDto(userService.getUsernameById(it.authorId))
            }
    }

    override fun updateProblemTexts(problemId: String, problemTextsDto: ProblemTextsDto) {
        val userId = userService.getCurrentUserId()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source ${problem.sourceId}")
        }
        val updatedProblem = problem.copy(
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
        val updatedProblem = problem.copy(
            problemImagePath = ""
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
        val updatedProblem = problem.copy(
            answerImagePath = ""
        )
        problemRepository.update(updatedProblem, problemId)
        unapproveSource(source)
    }

    override fun uploadProblemImage(problemId: String, image: MultipartFile): String {
        val userId = userService.getCurrentUserId()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source ${problem.sourceId}")
        }
        val imagesUUID = UUID.randomUUID()
        val problemImagePath = "problems/${imagesUUID}_${image.originalFilename}"
        storageRepository.uploadImage(image, problemImagePath)
        val updatedProblem = problem.copy(
            problemImagePath = problemImagePath
        )
        problemRepository.update(updatedProblem, problemId)
        unapproveSource(source)
        return problemImagePath
    }

    override fun uploadAnswerImage(problemId: String, image: MultipartFile): String {
        val userId = userService.getCurrentUserId()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId != userId) {
            throw IllegalAccessException("User $userId does not own source ${problem.sourceId}")
        }
        val imagesUUID = UUID.randomUUID()
        val answerImagePath = "answers/${imagesUUID}_${image.originalFilename}"
        storageRepository.uploadImage(image, answerImagePath)
        val updatedProblem = problem.copy(
            answerImagePath = answerImagePath
        )
        problemRepository.update(updatedProblem, problemId)
        unapproveSource(source)
        return answerImagePath
    }

    private fun unapproveSource(source: Source) {
        val modifiedSource = source.copy(
            lastModifiedOn = Instant.now().toString(),
            reviewStatus = ReviewStatus.PENDING
        )
        sourceRepository.update(modifiedSource, source.id)
        val problems = problemRepository.getBySourceId(source.id)
        problems.forEach {
            problemRepository.update(it.copy(isApproved = false), it.id)
        }
    }
}
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
        val username = userService.getCurrentUserUsername()

        val createdSource = sourceRepository.create(
            Source(
                name = sourceData.name,
                description = sourceData.description,
                author = username
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
        val username = userService.getCurrentUserUsername()
        val imagesUUID = UUID.randomUUID()

        val createdProblem = problemRepository.create(
            Problem(
                problemText = problem.problemText,
                problemImagePath = problemService.utilsGetNewPath(problem.problemImageUrl, if (problemImageFile == null) "" else "problems/${imagesUUID}.${
                    problemImageFile.originalFilename?.split(".")?.last() ?: ""
                }"),
                answerText = problem.answerText,
                answerImagePath = problemService.utilsGetNewPath(problem.answerImageUrl, if (answerImageFile == null) "" else "answers/${imagesUUID}.${
                    answerImageFile.originalFilename?.split(".")?.last() ?: ""
                }"),
                sourceId = sourceId
            )
        )

        problemImageFile?.let {
            storageRepository.uploadImage(problemImageFile, "problems/${imagesUUID}.${it.originalFilename?.split(".")?.last()}")
        }
        answerImageFile?.let {
            storageRepository.uploadImage(answerImageFile, "answers/${imagesUUID}.${it.originalFilename?.split(".")?.last()}")
        }

        return createdProblem.id
    }

    override fun getMySources(): List<Source> {
        val username = userService.getCurrentUserUsername()
        return sourceRepository.getByAuthor(username)
    }

    override fun getProblemsBySource(sourceId: String): List<ProblemDisplayViewDto> {
        val username = userService.getCurrentUserUsername()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        if (source.author != username  && source.reviewStatus != ReviewStatus.APPROVED) {
            userService.grantRoleAtLeast(Role.ADMIN)
        }

        return problemRepository.getBySourceId(sourceId)
            .map {
                ProblemDisplayViewDto(
                    id = it.id,
                    skfCode = it.skfCode,
                    problemText = it.problemText,
                    problemImageSrc = problemService.utilsGetImageSrc(it.problemImagePath),
                    answerText = it.answerText,
                    answerImageSrc = problemService.utilsGetImageSrc(it.answerImagePath),
                    sourceId = it.sourceId,
                    categoryId = it.categoryId
                )
            }
    }

    override fun approve(sourceId: String, reviewMessage: String): Source {
        val username = userService.getCurrentUserUsername()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        val updatedSource = source.copy(
            reviewStatus = ReviewStatus.APPROVED,
            reviewMessage = reviewMessage,
            reviewedBy = username,
            reviewedOn = Instant.now().toString()
        )
        sourceRepository.update(updatedSource, sourceId)

        val problems = problemRepository.getBySourceId(sourceId)
        problems.forEach {
            val skfCode = metaService.getIncrementedLastUsedSkfCode()
            metaService.incrementLastUsedSkfCode()
            val updatedProblem = it.copy(
                skfCode = skfCode,
                isApproved = true
            )
            problemRepository.update(updatedProblem, it.id)
        }
        return updatedSource
    }

    override fun reject(sourceId: String, reviewMessage: String): Source {
        val username = userService.getCurrentUserUsername()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        val updatedSource = source.copy(
            reviewStatus = ReviewStatus.REJECTED,
            reviewMessage = reviewMessage,
            reviewedBy = username,
            reviewedOn = Instant.now().toString()
        )
        sourceRepository.update(updatedSource, sourceId)

        val problems = problemRepository.getBySourceId(sourceId)
        problems.forEach {
            val updatedProblem = it.copy(
                skfCode = "",
                isApproved = false
            )
            problemRepository.update(updatedProblem, it.id)
        }
        return updatedSource
    }

    override fun deleteSource(sourceId: String) {
        val username = userService.getCurrentUserUsername()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        if (source.author != username) {
            throw IllegalAccessException("User $username does not own source $sourceId")
        }
        val problems = problemRepository.getBySourceId(sourceId)
        if (problems.isNotEmpty()) {
            throw IllegalAccessException("Source $sourceId has problems, delete them first")
        }
        sourceRepository.delete(sourceId)
    }

    override fun deleteProblem(problemId: String) {
        val username = userService.getCurrentUserUsername()
        val problem = problemRepository.findById(problemId) ?: throw NotFoundException("Problem not found")
        val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
        if (source.author != username) {
            throw IllegalAccessException("User $username does not own source ${problem.sourceId}")
        }
        problemRepository.delete(problemId)
        if (problem.problemImagePath.startsWith("problems/")) {
            storageRepository.deleteImage(problem.problemImagePath)
        }
        if (problem.answerImagePath.startsWith("answers/")) {
            storageRepository.deleteImage(problem.answerImagePath)
        }
        val modifiedSource = source.copy(
            lastModifiedOn = Instant.now().toString(),
            reviewStatus = ReviewStatus.PENDING
        )
        sourceRepository.update(modifiedSource, problem.sourceId)
    }

    override fun updateSource(sourceId: String, sourceData: SourceSubmitDto): Source {
        val username = userService.getCurrentUserUsername()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        if (source.author != username) {
            throw IllegalAccessException("User $username does not own source $sourceId")
        }
        val updatedSource = source.copy(
            name = sourceData.name,
            description = sourceData.description,
            lastModifiedOn = Instant.now().toString(),
            reviewStatus = ReviewStatus.PENDING
        )
        sourceRepository.update(updatedSource, sourceId)
        return updatedSource
    }

    override fun updateProblem(
        problemId: String,
        problem: ProblemSubmitDto,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?
    ): Problem {
        TODO("Not yet implemented")
    }
}
package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.*
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.Source
import lt.skafis.bankas.repository.ProblemRepository
import lt.skafis.bankas.repository.SourceRepository
import lt.skafis.bankas.repository.StorageRepository
import lt.skafis.bankas.service.ApprovalService
import lt.skafis.bankas.service.ProblemService
import lt.skafis.bankas.service.SourceService
import lt.skafis.bankas.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.util.*

@Service
class ApprovalServiceImpl: ApprovalService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var sourceRepository: SourceRepository

    @Autowired
    private lateinit var problemRepository: ProblemRepository

    @Autowired
    private lateinit var storageRepository: StorageRepository

    override fun submitSourceData(sourceData: SourceSubmitDto): String {
        val username = userService.getCurrentUserUsername()

        log.info("Creating source in firestore by user: $username")
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

        log.info("Creating problem in firestore by user: $username")
        val createdProblem = problemRepository.create(
            Problem(
                problemText = problem.problemText,
                problemImagePath = getNewPath(problem.problemImageUrl, if (problemImageFile == null) "" else "problems/${imagesUUID}.${
                    problemImageFile.originalFilename?.split(".")?.last() ?: ""
                }"),
                answerText = problem.answerText,
                answerImagePath = getNewPath(problem.answerImageUrl, if (answerImageFile == null) "" else "answers/${imagesUUID}.${
                    answerImageFile.originalFilename?.split(".")?.last() ?: ""
                }"),
                sourceId = sourceId
            )
        )

        log.info("Uploading images to storage by user: $username")
        problemImageFile?.let {
            storageRepository.uploadImage(problemImageFile, "problems/${imagesUUID}.${it.originalFilename?.split(".")?.last()}")
        }
        answerImageFile?.let {
            storageRepository.uploadImage(answerImageFile, "answers/${imagesUUID}.${it.originalFilename?.split(".")?.last()}")
        }

        return createdProblem.id
    }

    private fun getNewPath(imageUrl: String, storagePathOrEmpty: String): String =
        if (imageUrl.isNotEmpty() && storagePathOrEmpty.isEmpty()) {
            if (isValidUrl(imageUrl)) {
                URI(imageUrl)
                imageUrl
            } else {
                throw IllegalArgumentException("Invalid URL: $imageUrl")
            }
        } else if (imageUrl.isEmpty() && storagePathOrEmpty.isNotEmpty()) {
            storagePathOrEmpty
        } else if (imageUrl.isEmpty() && storagePathOrEmpty.isEmpty()) {
            ""
        } else {
            throw IllegalArgumentException("Invalid image input (only one image for question and one image for answer is allowed per problem)")
        }

    private fun isValidUrl(url: String): Boolean {
        val regex = Regex("https://.*\\.(jpeg|gif|png|apng|svg|bmp|ico)")
        return regex.matches(url)
    }
}
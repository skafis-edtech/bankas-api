package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.dto.SourcePostDto
import lt.skafis.bankas.dto.SourceWithProblemsSubmitDto
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

@Service
class ApprovalServiceImpl: ApprovalService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var sourceService: SourceService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var problemService: ProblemService

    @Autowired
    private lateinit var storageRepository: StorageRepository

    override fun submitSourceWithProblems(
        sourceData: SourceWithProblemsSubmitDto,
        problemImageFiles: List<MultipartFile>,
        answerImageFiles: List<MultipartFile>
    ): String {
        val username = userService.getCurrentUserUsername()

        log.info("Creating source in firestore by user: $username")
        val createdSource = sourceService.createSource(
            SourcePostDto(
            name = sourceData.name,
            description = sourceData.description,
            author = username
        )
        )

        log.info("Creating problems in firestore")
        sourceData.problems.forEachIndexed { _, problem ->
            problemService.createProblem(
                ProblemPostDto(
                    problemText = problem.problemText,
                    problemImagePath = getNewPath(problem.problemImageUrl, problem.problemImageFilename, "problems"),
                    answerText = problem.answerText,
                    answerImagePath = getNewPath(problem.answerImageUrl, problem.answerImageFilename, "answers"),
                    sourceId = createdSource.id
                )
            )
        }

        log.info("Uploading images to storage")
        problemImageFiles.forEachIndexed { _, file ->
            storageRepository.uploadImage(file, "problems/${file.originalFilename}")
        }
        answerImageFiles.forEachIndexed { _, file ->
            storageRepository.uploadImage(file, "answers/${file.originalFilename}")
        }

        return createdSource.id
    }

    private fun getNewPath(imageUrl: String, imageFilename: String, dirName: String): String =
        if (imageUrl.isNotEmpty() && imageFilename.isEmpty()) {
            if (isValidUrl(imageUrl)) {
                URI(imageUrl)
                imageUrl
            } else {
                throw IllegalArgumentException("Invalid URL: $imageUrl")
            }
        } else if (imageUrl.isEmpty() && imageFilename.isNotEmpty()) {
            "$dirName/$imageFilename"
        } else if (imageUrl.isEmpty() && imageFilename.isEmpty()) {
            ""
        } else {
            throw IllegalArgumentException("Invalid image input (only one image for question and one image for answer is allowed per problem)")
        }

    private fun isValidUrl(url: String): Boolean {
        val regex = Regex("https://.*\\.(jpeg|gif|png|apng|svg|bmp|ico)")
        return regex.matches(url)
    }
}
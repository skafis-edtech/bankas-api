package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.dto.UnderReviewProblemDisplayViewDto
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.model.UnderReviewProblem
import lt.skafis.bankas.repository.FirestoreProblemRepository
import lt.skafis.bankas.repository.FirestoreUnderReviewProblemRepository
import lt.skafis.bankas.repository.StorageRepository
import org.apache.logging.log4j.util.InternalException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.webjars.NotFoundException
import java.net.URI

@Service
class ProblemServiceImpl(
    val firestoreProblemRepository: FirestoreProblemRepository,
    val storageRepository: StorageRepository,
    val problemMetaService: ProblemMetaService,
    val firestoreUnderReviewProblemRepository: FirestoreUnderReviewProblemRepository,
    val userService: UserService
) : ProblemService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getPublicProblemById(id: String): ProblemDisplayViewDto {
        log.info("Fetching problem by id: $id")
        val problem = firestoreProblemRepository.getProblemById(id) ?: throw NotFoundException("Problem not found")
        return problemMapToProblemDisplay(problem)
    }

    override fun getPublicProblemsByCategoryId(categoryId: String): List<ProblemDisplayViewDto> {
        log.info("Fetching problems by category id: $categoryId")
        val problems = firestoreProblemRepository.getProblemsByCategoryId(categoryId)
        val problemDisplayViewDtoList = problems.map { problem ->
            problemMapToProblemDisplay(problem)
        }
        log.info("Problems fetched successfully")
        return problemDisplayViewDtoList
    }

    override fun getPublicProblemCount(): CountDto {
        log.info("Fetching problems count")
        val count = firestoreProblemRepository.countDocuments()
        if (count == 0L) throw InternalException("Failed to count problems")
        log.info("Problems count fetched successfully")
        return CountDto(count)
    }

    override fun submitProblem(
        problem: ProblemPostDto,
        userId: String,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?
    ): UnderReviewProblem {

        val username = userService.getUsernameById(userId)
        log.info("Uploading images to under review storage (if provided) by user: $username")

        val newProblemId = firestoreUnderReviewProblemRepository.genNewProblemId()
        val problemExtension = problemImageFile?.originalFilename?.split(".")?.lastOrNull()
        val answerExtension = answerImageFile?.originalFilename?.split(".")?.lastOrNull()

        val problemImagePath = uploadImageAndGetPath(
            problem.problemImageUrl,
            problemImageFile,
            "/underReviewProblems/$newProblemId.$problemExtension"
        )

        val answerImagePath = uploadImageAndGetPath(
            problem.answerImageUrl,
            answerImageFile,
            "/underReviewAnswers/$newProblemId.$answerExtension"
        )

        log.info("UnderReviewProblem images uploaded successfully")
        log.info("Creating underReviewProblem in firestore for user: $userId")

        val problemToCreate = UnderReviewProblem(
            id = newProblemId,
            problemText = problem.problemText,
            answerText = problem.answerText,
            problemImagePath = problemImagePath,
            answerImagePath = answerImagePath,
            categoryId = problem.categoryId,
            author = username
        )
        val id = firestoreUnderReviewProblemRepository.createProblemWithGivenId(problemToCreate)
        if (id.isBlank()) throw InternalException("Failed to create underReviewProblem")

        log.info("UnderReviewProblem created in firestore successfully")
        return problemToCreate
    }


    //OLD STUFF

    override fun updateProblem(
        id: String,
        problem: ProblemPostDto,
        userId: String,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?
    ): Problem {
        TODO("Not yet implemented")
    }

    override fun deleteProblem(id: String, userId: String): Boolean {
        TODO("Not yet implemented")
    }



//
//    override fun updateProblem(id: String, problem: ProblemPostDto, userId: String, problemImageFile: MultipartFile?, answerImageFile: MultipartFile?): ProblemViewDto {
//        TODO("Not yet implemented")
//    }
//
//    override fun deleteProblem(id: String, userId: String): Boolean {
//        TODO("Not yet implemented")
//    }


    private fun getImageSrc(imagePath: String): String {
        return imagePath.let {
            if (isValidUrl(it)) {
                // Validate the URL to prevent injection attacks
                URI(it)// This will throw an exception if the URL is not valid
                it
            } else if (
                it.startsWith("problems/") ||
                it.startsWith("answers/") ||
                it.startsWith("underReviewProblems/") ||
                it.startsWith("underReviewAnswers/")
                ) {
                // Assume that if the path starts with "problems/" or "answers/" or "underReviewProblems/" or "underReviewAnswers/", it's a storage path
                storageRepository.getImageUrl(it)
            } else if (it.isEmpty()) {
                ""
            } else {
                throw IllegalArgumentException("Invalid image path: $it")
            }
        }
    }

    private fun isValidUrl(url: String): Boolean {
        val regex = Regex("https://.*\\.(jpeg|gif|png|apng|svg|bmp|ico)")
        return regex.matches(url)
    }

    private fun uploadImageAndGetPath(url: String, file: MultipartFile?, fullPath: String): String =
        /*
        Input (same for answerImageUrl):
        - CASE1: problem.problemImageUrl is https://... string AND problemImageFile is null >>> Then just return problemImagePath = problem.problemImage
        - CASE2: problem.problemImageUrl is "" AND problemImageFile is not null >>> Then upload the file to storage and return problemImagePath = "problems/$newSkfCode"
        - CASE3: problem.problemImageUrl is "" AND problemImageFile is null >>> Then return problemImagePath = ""
         */

        if (url.isNotEmpty() && file == null) {
            //CASE1
            if (isValidUrl(url)) {
                URI(url)
                url
            } else {
                throw IllegalArgumentException("Invalid URL: ${url}")
            }
        } else if (url.isEmpty() && file != null) {
            //CASE2
            val mediaLink = storageRepository.uploadImage(file, fullPath)
            log.info("$fullPath image uploaded: $mediaLink")
            fullPath
        } else if (url.isEmpty() && file == null) {
            //CASE3
            ""
        } else {
            throw IllegalArgumentException("Invalid $fullPath image input")
        }


    private fun problemMapToProblemDisplay(problem: Problem): ProblemDisplayViewDto {
        return ProblemDisplayViewDto(
            id = problem.id,
            skfCode = problem.skfCode,
            problemText = problem.problemText,
            problemImageSrc = getImageSrc(problem.problemImagePath),
            answerText = problem.answerText,
            answerImageSrc = getImageSrc(problem.answerImagePath),
            categoryId = problem.categoryId,
            author = problem.author,
            approvedBy = problem.approvedBy,
            approvedOn = problem.approvedOn,
            createdOn = problem.createdOn,
            lastModifiedOn = problem.lastModifiedOn,
        )
    }

    private fun underReviewProblemMapToProblemDisplay(problem: UnderReviewProblem): UnderReviewProblemDisplayViewDto {
        return UnderReviewProblemDisplayViewDto(
            id = problem.id,
            problemText = problem.problemText,
            answerText = problem.answerText,
            problemImageSrc = getImageSrc(problem.problemImagePath),
            answerImageSrc = getImageSrc(problem.answerImagePath),
            categoryId = problem.categoryId,
            author = problem.author,
            createdOn = problem.createdOn,
            lastModifiedOn = problem.lastModifiedOn,
            reviewStatus = problem.reviewStatus,
            rejectedBy = problem.rejectedBy,
            rejectedOn = problem.rejectedOn,
            rejectionMessage = problem.rejectionMessage,
        )
    }

}
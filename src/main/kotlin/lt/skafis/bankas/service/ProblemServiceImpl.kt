package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.dto.UnderReviewProblemDisplayViewDto
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.model.UnderReviewProblem
import lt.skafis.bankas.repository.FirestoreProblemRepository
import lt.skafis.bankas.repository.StorageRepository
import org.apache.logging.log4j.util.InternalException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.webjars.NotFoundException
import java.net.URI
import java.time.Instant

@Service
class ProblemServiceImpl (
    val firestoreProblemRepository: FirestoreProblemRepository,
    val storageRepository: StorageRepository,
    val problemMetaService: ProblemMetaService
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
    //Unimplemented stuff


    //OLD STUFF
    override fun createProblem(
        problem: ProblemPostDto,
        userId: String,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?
    ): Problem {
        TODO("Not yet implemented")
    }

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


//    override fun createProblem(problem: ProblemPostDto, userId: String, problemImageFile: MultipartFile?, answerImageFile: MultipartFile?): ProblemViewDto {
//
//        log.info("Uploading images to storage (if provided) by user: $userId")
//
//        val newProblemCode = problemMetaService.getIncrementedLastUsedSkfCode()
//        val problemExtension = problemImageFile?.originalFilename?.split(".")?.lastOrNull()
//        val answerExtension = answerImageFile?.originalFilename?.split(".")?.lastOrNull()
//
//        var problemImagePath: String? = null
//        var answerImagePath: String? = null
//
//        /*
//        Input (same for answerImage):
//        - CASE1: problem.problemImage is https://... string AND problemImageFile is null >>> Then just return problemImagePath = problem.problemImage
//        - CASE2: problem.problemImage is null AND problemImageFile is not null >>> Then upload the file to storage and return problemImagePath = "problems/$newSkfCode"
//        - CASE3: problem.problemImage is null AND problemImageFile is null >>> Then return problemImagePath = null
//         */
//
//        if (!problem.problemImage.isNullOrEmpty() && problemImageFile == null) {
//            //CASE1
//            if (isValidUrl(problem.problemImage)) {
//                problemImagePath = problem.problemImage
//            } else {
//                throw IllegalArgumentException("Invalid URL: ${problem.problemImage}")
//            }
//        } else if (problem.problemImage.isNullOrEmpty() && problemImageFile != null) {
//            //CASE2
//            problemImagePath = "problems/$newProblemCode.$problemExtension"
//            val mediaLink = problemStorageRepository.uploadImage(problemImageFile, "$newProblemCode.$problemExtension")
//            log.info("Problem image uploaded: $mediaLink")
//        } else if (problem.problemImage.isNullOrEmpty() && problemImageFile == null) {
//            //CASE3
//            problemImagePath = null
//        } else {
//            throw IllegalArgumentException("Invalid problem image input")
//        }
//
//        if (!problem.answerImage.isNullOrEmpty() && answerImageFile == null) {
//            //CASE1
//            if (isValidUrl(problem.answerImage)) {
//                answerImagePath = problem.answerImage
//            } else {
//                throw IllegalArgumentException("Invalid URL: ${problem.answerImage}")
//            }
//        } else if (problem.answerImage.isNullOrEmpty() && answerImageFile != null) {
//            //CASE2
//            answerImagePath = "answers/$newProblemCode.$answerExtension"
//            val mediaLink = answerStorageRepository.uploadImage(answerImageFile, "$newProblemCode.$answerExtension")
//            log.info("Answer image uploaded: $mediaLink")
//        } else if (problem.answerImage.isNullOrEmpty() && answerImageFile == null) {
//            //CASE3
//            answerImagePath = null
//        } else {
//            throw IllegalArgumentException("Invalid answer image input")
//        }
//
//        log.info("Problem images uploaded successfully")
//
//        log.info("Creating problem in firestore for user: $userId")
//
//        val problemToCreate = ProblemViewDto(
//            id = newProblemCode,
//            problemText = problem.problemText,
//            answerText = problem.answerText,
//            problemImage = problemImagePath,
//            answerImage = answerImagePath,
//            categoryId = problem.categoryId,
//            createdOn = Instant.now().toString()
//        )
//        val id = firestoreProblemRepository.createProblem(problemToCreate)
//        if (id.isBlank()) throw InternalException("Failed to create problem")
//        problemMetaService.incrementLastUsedSkfCode()
//
//        log.info("Problem created in firestore successfully")
//        return problemToCreate
//    }
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
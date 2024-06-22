package lt.skafis.bankas.service

import com.google.cloud.storage.Bucket
import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.dto.UnderReviewProblemDisplayViewDto
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.model.ReviewStatus
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.UnderReviewProblem
import lt.skafis.bankas.repository.FirestoreProblemRepository
import lt.skafis.bankas.repository.FirestoreUnderReviewProblemRepository
import lt.skafis.bankas.repository.StorageRepository
import org.apache.logging.log4j.util.InternalException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.threeten.bp.Instant
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

    override fun getPublicProblemBySkfCode(skfCode: String): ProblemDisplayViewDto {
        log.info("Fetching problem by skfCode: $skfCode")
        val problem = firestoreProblemRepository.getProblemBySkfCode(skfCode) ?: throw NotFoundException("Problem not found")
        return problemMapToProblemDisplay(problem)
    }

    override fun getPublicProblemById(id: String): Problem {
        log.info("Fetching problem by id: $id")
        val problem = firestoreProblemRepository.getProblemById(id) ?: throw NotFoundException("Problem not found")
        log.info("Problem fetched successfully")
        return problem
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
            "underReviewProblems/$newProblemId.$problemExtension"
        )

        val answerImagePath = uploadImageAndGetPath(
            problem.answerImageUrl,
            answerImageFile,
            "underReviewAnswers/$newProblemId.$answerExtension"
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

    override fun getAllUnderReviewProblems(userId: String): List<UnderReviewProblemDisplayViewDto> {
        val role = userService.getRoleById(userId)
        if (role != Role.ADMIN) throw IllegalStateException("User is not an admin")
        val username = userService.getUsernameById(userId)

        log.info("Fetching all under review problems by user: $username")
        val problems = firestoreUnderReviewProblemRepository.getAllProblems()
        val underReviewProblemDisplayViewDtoList = problems.map { problem ->
            underReviewProblemMapToProblemDisplay(problem)
        }
        log.info("Under review problems fetched successfully")
        return underReviewProblemDisplayViewDtoList
    }

    override fun approveProblem(id: String, userId: String): Problem {
        val role = userService.getRoleById(userId)
        if (role != Role.ADMIN) throw IllegalStateException("User is not an admin")
        val username = userService.getUsernameById(userId)

        log.info("Approving problem $id by user: $username")

        val newSkfId = problemMetaService.getIncrementedLastUsedSkfCode()
        val problemToApprove = firestoreUnderReviewProblemRepository.getProblemById(id) ?: throw NotFoundException("Problem not found")
        val newProblemImagePath = underReviewImagePathToMainPath(problemToApprove.problemImagePath, newSkfId)
        val newAnswerImagePath = underReviewImagePathToMainPath(problemToApprove.answerImagePath, newSkfId)
        val newProblem = Problem(
            id = id,
            skfCode = newSkfId,
            problemText = problemToApprove.problemText,
            problemImagePath = newProblemImagePath,
            answerText = problemToApprove.answerText,
            answerImagePath = newAnswerImagePath,
            categoryId = problemToApprove.categoryId,
            author = problemToApprove.author,
            approvedBy = username,
            createdOn = problemToApprove.createdOn,
            lastModifiedOn = problemToApprove.lastModifiedOn
        )
        firestoreProblemRepository.createProblemWithSpecifiedId(newProblem)
        problemMetaService.incrementLastUsedSkfCode()
        log.info("Problem created in main collection")

        val success = firestoreUnderReviewProblemRepository.deleteProblem(id)
        if (!success) throw InternalException("Failed to delete problem from under review collection")
        log.info("Problem deleted from under review collection")

        if (problemToApprove.problemImagePath.isNotEmpty() && !isValidUrl(problemToApprove.problemImagePath) && problemToApprove.problemImagePath.startsWith("underReviewProblems/")
             && newProblemImagePath.isNotEmpty() && !isValidUrl(newProblemImagePath) && newProblemImagePath.startsWith("problems/")
            ) {
            copyImageInFirebaseStorage(problemToApprove.problemImagePath, newProblemImagePath)
            storageRepository.deleteImage(problemToApprove.problemImagePath)
        }
        if (problemToApprove.answerImagePath.isNotEmpty() && !isValidUrl(problemToApprove.answerImagePath) && problemToApprove.answerImagePath.startsWith("underReviewAnswers/")
             && newAnswerImagePath.isNotEmpty() && !isValidUrl(newAnswerImagePath) && newAnswerImagePath.startsWith("answers/"))
            {
            copyImageInFirebaseStorage(problemToApprove.answerImagePath, newAnswerImagePath)
            storageRepository.deleteImage(problemToApprove.answerImagePath)
        }
        log.info("Problem images (if exist) copied to main storage and deleted from review storage")

        log.info("Problem approved successfully")
        return newProblem
    }

    override fun getAllUnderReviewProblemsForAuthor(userId: String): List<UnderReviewProblemDisplayViewDto> {
        val username = userService.getUsernameById(userId)
        log.info("Fetching all under review problems for author: $username")
        val problems = firestoreUnderReviewProblemRepository.getProblemsByAuthor(username)
        val underReviewProblemDisplayViewDtoList = problems.map { problem ->
            underReviewProblemMapToProblemDisplay(problem)
        }
        log.info("Under review problems fetched successfully")
        return underReviewProblemDisplayViewDtoList
    }

    override fun getAllApprovedProblemsForAuthor(userId: String): List<ProblemDisplayViewDto> {
        val username = userService.getUsernameById(userId)
        log.info("Fetching all approved problems for author: $username")
        val problems = firestoreProblemRepository.getProblemsByAuthor(username)
        val problemDisplayViewDtoList = problems.map { problem ->
            problemMapToProblemDisplay(problem)
        }
        log.info("Approved problems fetched successfully")
        return problemDisplayViewDtoList
    }

    override fun rejectProblem(id: String, rejectMsg: String, userId: String): UnderReviewProblem {
        val role = userService.getRoleById(userId)
        if (role != Role.ADMIN) throw IllegalStateException("User is not an admin")
        val username = userService.getUsernameById(userId)

        log.info("Rejecting problem $id by user: $username")

        val problemToReject = firestoreUnderReviewProblemRepository.getProblemById(id) ?: throw NotFoundException("Problem not found")
        val rejectedProblem = problemToReject.copy(
            reviewStatus = ReviewStatus.REJECTED,
            rejectedBy = username,
            rejectedOn = Instant.now().toString(),
            rejectionMessage = rejectMsg
        )
        val success = firestoreUnderReviewProblemRepository.updateProblem(rejectedProblem)
        if (!success) throw InternalException("Failed to reject problem")
        log.info("Problem rejected successfully")
        return rejectedProblem
    }

    override fun updateMyUnderReviewProblem(
        id: String,
        problem: ProblemPostDto,
        userId: String,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?
    ): UnderReviewProblem {
        val username = userService.getUsernameById(userId)
        val currentProblem = firestoreUnderReviewProblemRepository.getProblemById(id) ?: throw NotFoundException("Problem not found")
        if (currentProblem.author != username) throw IllegalStateException("User is not the author of the problem")

        log.info("Uploading images to under review storage (if provided) by user: $username")

        val problemExtension = problemImageFile?.originalFilename?.split(".")?.lastOrNull()
        val answerExtension = answerImageFile?.originalFilename?.split(".")?.lastOrNull()

        val success1 = deleteImgIfExists(currentProblem.problemImagePath)
        if (!success1) throw InternalException("Failed to delete problem image")
        val problemImagePath = uploadImageAndGetPath(
            problem.problemImageUrl,
            problemImageFile,
            "underReviewProblems/$id.$problemExtension"
        )

        val success2 = deleteImgIfExists(currentProblem.answerImagePath)
        if (!success2) throw InternalException("Failed to delete answer image")
        val answerImagePath = uploadImageAndGetPath(
            problem.answerImageUrl,
            answerImageFile,
            "underReviewAnswers/$id.$answerExtension"
        )

        log.info("UnderReviewProblem images uploaded successfully")
        log.info("Updating underReviewProblem in firestore for user: $userId")

        val updatedProblem = currentProblem.copy(
            problemText = problem.problemText,
            answerText = problem.answerText,
            problemImagePath = problemImagePath,
            answerImagePath = answerImagePath,
            categoryId = problem.categoryId,
            lastModifiedOn = Instant.now().toString(),
            reviewStatus = ReviewStatus.PENDING
        )
        val success = firestoreUnderReviewProblemRepository.updateProblem(updatedProblem)
        if (!success) throw InternalException("Failed to update underReviewProblem")
        log.info("UnderReviewProblem updated in firestore successfully")
        return updatedProblem
    }

    override fun deleteMyUnderReviewProblem(id: String, userId: String): Boolean {
        val username = userService.getUsernameById(userId)
        val currentProblem = firestoreUnderReviewProblemRepository.getProblemById(id) ?: throw NotFoundException("Problem not found")
        if (currentProblem.author != username) throw IllegalStateException("User is not the author of the problem")

        log.info("Deleting under review problem $id by user: $username")

        val success1 = deleteImgIfExists(currentProblem.problemImagePath)
        if (!success1) throw InternalException("Failed to delete problem image")
        val success2 = deleteImgIfExists(currentProblem.answerImagePath)
        if (!success2) throw InternalException("Failed to delete answer image")

        val success3 = firestoreUnderReviewProblemRepository.deleteProblem(id)
        if (!success3) throw InternalException("Failed to delete problem")
        log.info("Under review problem deleted successfully")
        return true
    }

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

    private fun deleteImgIfExists(url: String): Boolean {
        if (url.isNotEmpty() && !isValidUrl(url)) {
            storageRepository.deleteImage(url)
        }
        return true
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

    private fun underReviewImagePathToMainPath(imagePath: String, newSkfId: String): String {
        return if (imagePath.startsWith("underReviewProblems/") || imagePath.startsWith("underReviewAnswers/")) {
            val replImg = imagePath.replace("underReviewProblems/", "problems/").replace("underReviewAnswers/", "answers/")
            replImg.replaceAfterLast("/", "$newSkfId.${replImg.substringAfterLast(".")}") //WARNING: some magic happening here!
        } else {
            imagePath
        }
    }

    private fun copyImageInFirebaseStorage(sourcePath: String, destinationPath: String) {
        val sourceBlob = storageRepository.getBlob(sourcePath)
        val bucket: Bucket = storageRepository.getBucket()

        if (sourceBlob != null) {
            val content = sourceBlob.getContent()
            val destinationBlob = bucket.create(destinationPath, content, sourceBlob.contentType)
            log.info("Image copied from $sourcePath to $destinationPath")
        } else {
            log.warn("Source image at $sourcePath not found")
        }
    }

}
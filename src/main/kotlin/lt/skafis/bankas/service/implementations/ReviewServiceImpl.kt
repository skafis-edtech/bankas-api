package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.ReviewStatus
import lt.skafis.bankas.repository.firestore.ProblemRepository
import lt.skafis.bankas.repository.firestore.SourceRepository
import lt.skafis.bankas.service.ReviewService
import lt.skafis.bankas.service.StorageService
import lt.skafis.bankas.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.threeten.bp.Instant
import org.webjars.NotFoundException

@Service
class ReviewServiceImpl : ReviewService {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var sourceRepository: SourceRepository

    @Autowired
    private lateinit var problemRepository: ProblemRepository

    @Autowired
    private lateinit var storageService: StorageService

    override fun approve(
        sourceId: String,
        reviewMessage: String,
    ): SourceDisplayDto {
        val username = userService.getCurrentUserUsername()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        val amendedReviewHistory =
            "${source.reviewHistory} \n ${Instant.now()} $username approved" +
                if (reviewMessage.isNotEmpty()) " with message: $reviewMessage" else "."
        val updatedSource =
            source.copy(
                reviewStatus = ReviewStatus.APPROVED,
                reviewHistory = amendedReviewHistory,
            )
        sourceRepository.update(updatedSource, sourceId)

        if (source.reviewStatus != ReviewStatus.APPROVED) {
            val problems = problemRepository.getBySourceId(sourceId)
            problems.forEach {
                val updatedProblem =
                    it.copy(
                        isApproved = true,
                    )
                problemRepository.update(updatedProblem, it.id)
            }
        }

        return updatedSource.toDisplayDto(userService.getUsernameById(updatedSource.authorId))
    }

    override fun reject(
        sourceId: String,
        reviewMessage: String,
    ): SourceDisplayDto {
        val username = userService.getCurrentUserUsername()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        val amendedReviewHistory =
            "${source.reviewHistory} \n ${Instant.now()} $username rejected" +
                if (reviewMessage.isNotEmpty()) " with message: $reviewMessage" else "."
        val updatedSource =
            source.copy(
                reviewStatus = ReviewStatus.REJECTED,
                reviewHistory = amendedReviewHistory,
            )
        sourceRepository.update(updatedSource, sourceId)

        if (source.reviewStatus == ReviewStatus.APPROVED) {
            val problems = problemRepository.getBySourceId(sourceId)
            problems.forEach {
                val updatedProblem =
                    it.copy(
                        isApproved = false,
                    )
                problemRepository.update(updatedProblem, it.id)
            }
        }
        return updatedSource.toDisplayDto(userService.getUsernameById(updatedSource.authorId))
    }

    override fun getPendingSources(
        page: Int,
        size: Int,
        search: String,
    ): List<SourceDisplayDto> =
        sourceRepository
            .getPendingSearchPageable(
                search,
                size,
                (page * size).toLong(),
            ).map {
                val count = problemRepository.countBySource(it.id)
                it.toDisplayDto(userService.getUsernameById(it.authorId), count.toInt())
            }

    override fun getProblemsBySource(
        sourceId: String,
        page: Int,
        size: Int,
    ): List<ProblemDisplayViewDto> =
        problemRepository
            .getBySourceIdPageable(sourceId, size, (page * size).toLong())
            .map {
                ProblemDisplayViewDto(
                    id = it.id,
                    sourceListNr = it.sourceListNr,
                    skfCode = it.skfCode,
                    problemText = it.problemText,
                    problemImageSrc = storageService.utilsGetImageSrc(it.problemImagePath),
                    answerText = it.answerText,
                    answerImageSrc = storageService.utilsGetImageSrc(it.answerImagePath),
                    sourceId = it.sourceId,
                    categories = it.categories,
                )
            }
}

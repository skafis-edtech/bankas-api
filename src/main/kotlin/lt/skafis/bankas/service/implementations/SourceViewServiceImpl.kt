package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.ReviewStatus
import lt.skafis.bankas.model.SortBy
import lt.skafis.bankas.repository.firestore.ProblemRepository
import lt.skafis.bankas.repository.firestore.SourceRepository
import lt.skafis.bankas.service.SourceViewService
import lt.skafis.bankas.service.StorageService
import lt.skafis.bankas.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class SourceViewServiceImpl : SourceViewService {
    @Autowired
    private lateinit var sourceRepository: SourceRepository

    @Autowired
    private lateinit var storageService: StorageService

    @Autowired
    private lateinit var problemRepository: ProblemRepository

    @Autowired
    private lateinit var userService: UserService

    override fun getSourceById(sourceId: String): SourceDisplayDto {
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source with id $sourceId not found")
        if (source.authorId == userService.getCurrentUserId() || source.reviewStatus == ReviewStatus.APPROVED) {
            val authorUsername = userService.getUsernameById(source.authorId)
            val count = problemRepository.countBySource(sourceId)
            return source.toDisplayDto(authorUsername, count.toInt())
        } else {
            throw IllegalAccessException("Unauthorized access")
        }
    }

    override fun getSourcesByAuthor(
        authorUsername: String,
        page: Int,
        size: Int,
        search: String,
        sortBy: SortBy,
    ): List<SourceDisplayDto> {
        val authorId = userService.getUserIdByUsername(authorUsername)
        return sourceRepository
            .getByAuthorSearchPageable(authorId, search, size, (page * size).toLong(), isApproved = true, sortBy)
            .map {
                val count = problemRepository.countBySource(it.id)
                it.toDisplayDto(authorUsername, count.toInt())
            }
    }

    override fun getProblemsBySource(
        sourceId: String,
        page: Int,
        size: Int,
    ): List<ProblemDisplayViewDto> {
        val userId = userService.getCurrentUserId()
        val source = sourceRepository.findById(sourceId) ?: throw NotFoundException("Source not found")
        if (source.authorId == userId || source.reviewStatus == ReviewStatus.APPROVED) {
            return problemRepository
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
        } else {
            throw IllegalAccessException("Unauthorized access")
        }
    }

    override fun getAvailableSources(
        page: Int,
        size: Int,
        search: String,
        sortBy: SortBy,
    ): List<SourceDisplayDto> {
        val userId = userService.getCurrentUserId()
        return sourceRepository
            .getAvailableSources(
                search,
                size,
                (page * size).toLong(),
                sortBy,
                userId,
            ).map {
                val count = problemRepository.countBySource(it.id)
                val authorUsername = userService.getUsernameById(it.authorId)
                it.toDisplayDto(authorUsername, count.toInt())
            }
    }
}

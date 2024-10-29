package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.model.ProblemVisibility
import lt.skafis.bankas.model.Role
import lt.skafis.bankas.model.Visibility
import lt.skafis.bankas.repository.firestore.ProblemRepository
import lt.skafis.bankas.repository.firestore.SourceRepository
import lt.skafis.bankas.service.StorageService
import lt.skafis.bankas.service.UserService
import lt.skafis.bankas.service.ViewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class ViewServiceImpl : ViewService {
    @Autowired
    private lateinit var sourceRepository: SourceRepository

    @Autowired
    private lateinit var storageService: StorageService

    @Autowired
    private lateinit var problemRepository: ProblemRepository

    @Autowired
    private lateinit var userService: UserService

    override fun getProblemsCount(): Long = problemRepository.countApproved()

    override fun getProblemBySkfCode(skfCode: String): ProblemDisplayViewDto {
        val problem = problemRepository.getBySkfCode(skfCode)
        if (problem == Problem()) {
            return ProblemDisplayViewDto(skfCode = skfCode, problemVisibility = ProblemVisibility.NOT_EXISTING)
        } else {
            val source = sourceRepository.findById(problem.sourceId) ?: throw NotFoundException("Source not found")
            val userId = userService.getCurrentUserId()
            val userData = userService.getUserById(userId)
            if (problem.isApproved ||
                source.authorId == userId ||
                userData.role == Role.ADMIN &&
                source.visibility == Visibility.PUBLIC
            ) {
                return ProblemDisplayViewDto(
                    id = problem.id,
                    sourceListNr = problem.sourceListNr,
                    skfCode = problem.skfCode,
                    problemText = problem.problemText,
                    problemImageSrc = storageService.utilsGetImageSrc(problem.problemImagePath),
                    answerText = problem.answerText,
                    answerImageSrc = storageService.utilsGetImageSrc(problem.answerImagePath),
                    categories = problem.categories,
                    sourceId = problem.sourceId,
                    problemVisibility = ProblemVisibility.VISIBLE,
                )
            } else {
                return ProblemDisplayViewDto(skfCode = skfCode, problemVisibility = ProblemVisibility.HIDDEN)
            }
        }
    }
}

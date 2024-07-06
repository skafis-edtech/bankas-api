package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.repository.ProblemRepository
import lt.skafis.bankas.service.ProblemService
import lt.skafis.bankas.service.UserService
import lt.skafis.bankas.modelOld.Role
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class ProblemServiceImpl(
    private val problemRepository: ProblemRepository,
    private val userService: UserService
): ProblemService {
    override fun getProblems(): List<Problem> {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        return problemRepository.findAll()
    }

    override fun getProblemById(id: String): Problem {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        return problemRepository.findById(id) ?: throw NotFoundException("Problem with id $id not found")
    }

    override fun createProblem(problemPostDto: ProblemPostDto): Problem {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        return problemRepository.create(
            Problem(
                skfCode = problemPostDto.skfCode,
                problemText = problemPostDto.problemText,
                problemImagePath = problemPostDto.problemImagePath,
                answerText = problemPostDto.answerText,
                answerImagePath = problemPostDto.answerImagePath,
                categoryId = problemPostDto.categoryId,
                sourceId = problemPostDto.sourceId
            )
        )
    }

    override fun updateProblem(id: String, problemPostDto: ProblemPostDto): Problem {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        var problemToUpdate = problemRepository.findById(id) ?: throw NotFoundException("Problem with id $id not found")
        problemToUpdate = problemToUpdate.copy(
            skfCode = problemPostDto.skfCode,
            problemText = problemPostDto.problemText,
            problemImagePath = problemPostDto.problemImagePath,
            answerText = problemPostDto.answerText,
            answerImagePath = problemPostDto.answerImagePath,
            categoryId = problemPostDto.categoryId,
            sourceId = problemPostDto.sourceId
        )
        val success = problemRepository.update(problemToUpdate, id)
        return if (success) problemToUpdate
        else throw Exception("Failed to update problem with id $id")
    }

    override fun deleteProblem(id: String) {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        val success = problemRepository.delete(id)
        if (!success) throw Exception("Failed to delete problem with id $id")
    }
}
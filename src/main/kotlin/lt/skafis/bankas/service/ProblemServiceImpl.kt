package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.dto.ProblemViewDto
import lt.skafis.bankas.repository.FirestoreProblemRepository
import lt.skafis.bankas.repository.ProblemStorageRepository
import org.apache.logging.log4j.util.InternalException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProblemServiceImpl (
    val firestoreProblemRepository: FirestoreProblemRepository,
    val problemStorageRepository: ProblemStorageRepository
) : ProblemService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getProblemById(id: String): ProblemViewDto {
        log.info("Fetching problem by id: $id")
        val problem = firestoreProblemRepository.getProblemById(id) ?: throw InternalException("Problem not found")
        log.info("Problem fetched successfully")
        TODO("Get images urls from storage")
        return problem
    }

    override fun getProblemsByCategoryId(categoryId: String): List<ProblemViewDto> {
        log.info("Fetching problems by category id: $categoryId")
        val problems = firestoreProblemRepository.getProblemsByCategoryId(categoryId)
        log.info("Problems fetched successfully")
        return problems
    }

    override fun createProblem(problem: ProblemPostDto, userId: String): ProblemViewDto {
        TODO("Not yet implemented")
    }

    override fun updateProblem(id: String, problem: ProblemPostDto, userId: String): ProblemViewDto {
        TODO("Not yet implemented")
    }

    override fun deleteProblem(id: String, userId: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getProblemCount(): CountDto {
        log.info("Fetching problems count")
        val count = firestoreProblemRepository.countDocuments()
        if (count == 0L) throw InternalException("Failed to count problems")
        log.info("Problems count fetched successfully")
        return CountDto(count)
    }
}
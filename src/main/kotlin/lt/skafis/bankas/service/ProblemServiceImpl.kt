package lt.skafis.bankas.service

import lt.skafis.bankas.dto.CountDto
import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.dto.ProblemViewDto
import lt.skafis.bankas.repository.FirestoreProblemRepository
import lt.skafis.bankas.repository.ProblemStorageRepository
import org.springframework.stereotype.Service

@Service
class ProblemServiceImpl (
    val firestoreProblemRepository: FirestoreProblemRepository,
    val problemStorageRepository: ProblemStorageRepository
) : ProblemService {
    override fun getProblemById(id: String): ProblemViewDto {
        TODO("Not yet implemented")
    }

    override fun getProblemsByCategoryId(categoryId: String): List<ProblemViewDto> {
        TODO("Not yet implemented")
    }

    override fun createProblem(problem: ProblemPostDto): ProblemViewDto {
        TODO("Not yet implemented")
    }

    override fun updateProblem(id: String, problem: ProblemPostDto): ProblemViewDto {
        TODO("Not yet implemented")
    }

    override fun deleteProblem(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getProblemCount(): CountDto {
        TODO("Not yet implemented")
    }
}
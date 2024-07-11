package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Problem
import lt.skafis.bankas.service.SortService
import org.springframework.stereotype.Service

@Service
class SortServiceImpl: SortService {
    override fun getSortedProblems(): List<ProblemDisplayViewDto> {
        TODO("Not yet implemented")
    }

    override fun getUnsortedProblems(): List<ProblemDisplayViewDto> {
        TODO("Not yet implemented")
    }

    override fun sortProblem(problemId: String, categoryId: String): Problem {
        TODO("Not yet implemented")
    }
}
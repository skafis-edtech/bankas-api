package lt.skafis.bankas.service

import lt.skafis.bankas.dto.ProblemDisplayViewDto

interface ViewService {
    fun getProblemsCount(): Long

    fun getProblemBySkfCode(skfCode: String): ProblemDisplayViewDto
}

package lt.skafis.bankas.service

import lt.skafis.bankas.dto.ProblemSubmitDto
import lt.skafis.bankas.dto.SourceSubmitDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.model.Source
import org.springframework.web.multipart.MultipartFile

interface ApprovalService {
    fun submitSourceData(sourceData: SourceSubmitDto): String
    fun submitProblem(
        sourceId: String,
        problem: ProblemSubmitDto,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?
    ): String
    fun getMySources(): List<Source>
    fun getProblemsBySource(sourceId: String): List<ProblemDisplayViewDto>
}
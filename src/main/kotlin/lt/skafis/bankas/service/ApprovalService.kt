package lt.skafis.bankas.service

import lt.skafis.bankas.dto.ProblemSubmitDto
import lt.skafis.bankas.dto.SourceSubmitDto
import org.springframework.web.multipart.MultipartFile

interface ApprovalService {
    fun submitSourceData(sourceData: SourceSubmitDto): String
    fun submitProblem(
        sourceId: String,
        problem: ProblemSubmitDto,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?
    ): String
}
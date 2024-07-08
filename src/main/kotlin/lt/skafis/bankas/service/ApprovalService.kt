package lt.skafis.bankas.service

import lt.skafis.bankas.dto.SourceWithProblemsSubmitDto
import org.springframework.web.multipart.MultipartFile

interface ApprovalService {
    fun submitSourceWithProblems(
        sourceData: SourceWithProblemsSubmitDto,
        problemImageFiles: List<MultipartFile>,
        answerImageFiles: List<MultipartFile>,
    ): String
}
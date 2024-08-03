package lt.skafis.bankas.service

import lt.skafis.bankas.dto.ProblemSubmitDto
import lt.skafis.bankas.dto.SourceSubmitDto
import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.Problem
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
    fun getMySources(): List<SourceDisplayDto>
    fun getProblemsBySource(sourceId: String): List<ProblemDisplayViewDto>
    fun approve(sourceId: String, reviewMessage: String): SourceDisplayDto
    fun reject(sourceId: String, reviewMessage: String): SourceDisplayDto
    fun deleteSource(sourceId: String)
    fun deleteProblem(problemId: String)
    fun updateSource(sourceId: String, sourceData: SourceSubmitDto): SourceDisplayDto
    fun updateProblem(
        problemId: String,
        problem: ProblemSubmitDto,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?
    ): Problem

    fun getSources(): List<SourceDisplayDto>
}
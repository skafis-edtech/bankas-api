package lt.skafis.bankas.service

import lt.skafis.bankas.dto.*
import lt.skafis.bankas.model.SortBy
import org.springframework.web.multipart.MultipartFile

interface ContentService {
    fun submitSourceData(sourceData: SourceSubmitDto): String

    fun submitProblem(
        sourceId: String,
        problem: ProblemSubmitDto,
        problemImageFile: MultipartFile?,
        answerImageFile: MultipartFile?,
    ): IdSkfDto

    fun getMySources(
        page: Int,
        size: Int,
        search: String,
        sortBy: SortBy,
    ): List<SourceDisplayDto>

    fun deleteSource(sourceId: String)

    fun deleteProblem(problemId: String)

    fun updateSource(
        sourceId: String,
        sourceData: SourceSubmitDto,
    ): SourceDisplayDto

    fun updateProblemTexts(
        problemId: String,
        problemTextsDto: ProblemTextsDto,
    )

    fun deleteProblemImage(problemId: String)

    fun deleteAnswerImage(problemId: String)

    fun uploadProblemImage(
        problemId: String,
        image: MultipartFile,
    ): String

    fun uploadAnswerImage(
        problemId: String,
        image: MultipartFile,
    ): String
}

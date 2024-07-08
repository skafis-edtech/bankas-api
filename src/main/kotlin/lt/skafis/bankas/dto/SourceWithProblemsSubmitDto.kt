package lt.skafis.bankas.dto

data class SourceWithProblemsSubmitDto (
    val name: String,
    val description: String,
    val problems: List<ProblemSubmitDto>
)

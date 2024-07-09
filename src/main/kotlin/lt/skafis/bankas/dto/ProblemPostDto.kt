package lt.skafis.bankas.dto

data class ProblemPostDto(
    val skfCode: String,
    val problemImagePath: String,
    val answerImagePath: String,
    val problemText: String,
    val answerText: String,
    val categoryId: String,
    val sourceId: String,
)
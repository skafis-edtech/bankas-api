package lt.skafis.bankas.dto

data class ProblemPostDto(
    val skfCode: String,
    val sourceListNr: Int,
    val problemImagePath: String,
    val answerImagePath: String,
    val problemText: String,
    val answerText: String,
    val categories: List<String>,
    val sourceId: String,
)
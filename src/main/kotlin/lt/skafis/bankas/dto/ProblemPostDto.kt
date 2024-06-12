package lt.skafis.bankas.dto

data class ProblemPostDto(
    val problemImage: String? = null,
    val answerImage: String? = null,
    val problemText: String? = null,
    val answerText: String? = null,
    val categoryId: String = "",
)
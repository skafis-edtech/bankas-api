package lt.skafis.bankas.dto

data class ProblemPostDto(
    val problemImageUrl: String = "", //should be empty if uploading image
    val answerImageUrl: String = "", //should be empty if uploading image
    val problemText: String = "",
    val answerText: String = "",
    val categoryId: String = "",
)
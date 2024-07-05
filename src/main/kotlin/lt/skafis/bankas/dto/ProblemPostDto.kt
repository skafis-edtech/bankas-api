package lt.skafis.bankas.dto

data class ProblemPostDto(
    val problemImageUrl: String = "", //should be empty if uploading image or no image
    val answerImageUrl: String = "", //should be empty if uploading image or no image
    val problemText: String = "",
    val answerText: String = "",
    val categoryId: String = "",
    val sourceId: String = "",
)
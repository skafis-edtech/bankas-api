package lt.skafis.bankas.dto

data class ProblemPostDto(
    val problemImage: String? = null, //should be null if uploading image
    val answerImage: String? = null, //should be null if uploading image
    val problemText: String? = null,
    val answerText: String? = null,
    val categoryId: String = "",
)
package lt.skafis.bankas.dto

data class ProblemViewDto(
    val id: String = "",
    val problemImage: String? = null,
    val answerImage: String? = null,
    val problemText: String? = null,
    val answerText: String? = null,
    val categoryId: String = "",
    val createdOn: String = "",
    )

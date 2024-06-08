package lt.skafis.bankas.model

data class Problem(
    val problemImage: String? = null,
    val answerImage: String? = null,
    val problemText: String? = null,
    val answerText: String? = null,
    val categoryId: String = "",
    val createdOn: String = "",
)

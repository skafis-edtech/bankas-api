package lt.skafis.bankas.model

import java.time.Instant

data class Problem (
    val id: String = "",
    val skfCode: String = "",
    val problemText: String = "",
    val problemImagePath: String = "",
    val answerText: String = "",
    val answerImagePath: String = "",
    val categoryId: String = "",
    val author: String = "",
    val approvedBy: String = "",
    val approvedOn: String = Instant.now().toString(),
    val createdOn: String = "",
    val lastModifiedOn: String = "",
)

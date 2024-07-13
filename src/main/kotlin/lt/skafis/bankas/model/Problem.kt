package lt.skafis.bankas.model

import com.google.cloud.firestore.annotation.PropertyName

data class Problem (
    override var id: String = "",
    val skfCode: String = "",
    val problemText: String = "",
    val problemImagePath: String = "",
    val answerText: String = "",
    val answerImagePath: String = "",
    val categoryId: String = "",
    val sourceId: String = "",

    @get:PropertyName("isApproved")
    val isApproved: Boolean = false,
): Identifiable
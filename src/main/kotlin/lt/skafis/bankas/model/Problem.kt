package lt.skafis.bankas.model

import com.google.cloud.firestore.annotation.PropertyName
import lt.skafis.bankas.config.AppConfig

data class Problem(
    override var id: String = "",
    val sourceListNr: Int = 0,
    val skfCode: String = "",
    val problemText: String = "",
    val problemImagePath: String = "",
    val answerText: String = "",
    val answerImagePath: String = "",
    val categories: List<String> = listOf(AppConfig().unsortedCategoryId),
    val sourceId: String = "",
    @get:PropertyName("isApproved")
    val isApproved: Boolean = false,
) : Identifiable

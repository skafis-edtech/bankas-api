package lt.skafis.bankas.model

import org.threeten.bp.Instant

data class Source(
    override var id: String = "",
    val name: String = "",
    val description: String = "",
    val reviewStatus: ReviewStatus = ReviewStatus.PENDING,
    val reviewedBy: String = "",
    val reviewedOn: String = "",
    val reviewMessage: String = "",
    val author: String = "",
    val createdOn: String = Instant.now().toString(),
    val lastModifiedOn: String = Instant.now().toString()
): Identifiable
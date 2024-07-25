package lt.skafis.bankas.dto

import lt.skafis.bankas.model.ReviewStatus

data class SourcePostDto(
    val name: String,
    val description: String,
    val reviewStatus: ReviewStatus,
    val reviewedById: String,
    val reviewedOn: String,
    val reviewMessage: String,
    val authorId: String,
    val createdOn: String,
    val lastModifiedOn: String,
)

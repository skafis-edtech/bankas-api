package lt.skafis.bankas.dto

import lt.skafis.bankas.model.ReviewStatus

data class SourcePostDto(
    val name: String,
    val description: String,
    val reviewStatus: ReviewStatus,
    val reviewedBy: String,
    val reviewedOn: String,
    val reviewMessage: String,
    val author: String,
    val createdOn: String,
    val lastModifiedOn: String
)

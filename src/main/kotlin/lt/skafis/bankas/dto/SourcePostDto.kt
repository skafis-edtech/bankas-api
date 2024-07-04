package lt.skafis.bankas.dto

import java.time.ZonedDateTime

data class SourcePostDto(
    val name: String,
    val description: String,
    val reviewedBy: String,
    val reviewedOn: ZonedDateTime,
    val reviewMessage: String,
    val createdOn: ZonedDateTime = ZonedDateTime.now(),
    val lastModifiedOn: ZonedDateTime
)

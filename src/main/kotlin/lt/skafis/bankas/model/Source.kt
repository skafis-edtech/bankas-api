package lt.skafis.bankas.model

import java.time.ZonedDateTime

data class Source(
    val id: String,
    val name: String,
    val description: String,
    val reviewStatus: String,
    val reviewedBy: String,
    val reviewedOn: ZonedDateTime,
    val reviewMessage: String,
    val createdOn: ZonedDateTime = ZonedDateTime.now(),
    val lastModifiedOn: ZonedDateTime
)
package lt.skafis.bankas.dto

import lt.skafis.bankas.model.Visibility

data class SourceSubmitDto(
    var name: String,
    var description: String,
    val visibility: Visibility,
)

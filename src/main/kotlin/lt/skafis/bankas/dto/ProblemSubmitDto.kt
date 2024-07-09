package lt.skafis.bankas.dto

data class ProblemSubmitDto(
    val problemImageUrl: String, //should be empty if uploading image or there's no image at all
    val answerImageUrl: String, //should be empty if uploading image or there's no image at all
    val problemText: String,
    val answerText: String,
)
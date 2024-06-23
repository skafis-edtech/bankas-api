package lt.skafis.bankas.dto

data class ProblemsForAuthor(
    val underReviewProblems: List<UnderReviewProblemDisplayViewDto>,
    val approvedProblems: List<ProblemDisplayViewDto>
)

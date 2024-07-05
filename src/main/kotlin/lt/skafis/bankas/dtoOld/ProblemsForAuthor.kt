package lt.skafis.bankas.dtoOld

data class ProblemsForAuthor(
    val underReviewProblems: List<UnderReviewProblemDisplayViewDto>,
    val approvedProblems: List<ProblemDisplayViewDto>
)

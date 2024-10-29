package lt.skafis.bankas.service

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto

interface ReviewService {
    fun approve(
        sourceId: String,
        reviewMessage: String,
    ): SourceDisplayDto

    fun reject(
        sourceId: String,
        reviewMessage: String,
    ): SourceDisplayDto

    fun getPendingSources(
        page: Int,
        size: Int,
        search: String,
    ): List<SourceDisplayDto>

    fun getProblemsBySource(
        sourceId: String,
        page: Int,
        size: Int,
    ): List<ProblemDisplayViewDto>
}

package lt.skafis.bankas.service

import lt.skafis.bankas.dto.ProblemDisplayViewDto
import lt.skafis.bankas.dto.SourceDisplayDto
import lt.skafis.bankas.model.SortBy

interface SourceViewService {
    fun getSourceById(sourceId: String): SourceDisplayDto

    fun getSourcesByAuthor(
        authorUsername: String,
        page: Int,
        size: Int,
        search: String,
        sortBy: SortBy,
    ): List<SourceDisplayDto>

    fun getProblemsBySource(
        sourceId: String,
        page: Int,
        size: Int,
    ): List<ProblemDisplayViewDto>

    fun getAvailableSources(
        page: Int,
        size: Int,
        search: String,
        sortBy: SortBy,
    ): List<SourceDisplayDto>
}

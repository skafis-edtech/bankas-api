package lt.skafis.bankas.service

import lt.skafis.bankas.dto.SourcePostDto
import lt.skafis.bankas.model.Source

interface SourceService {
    fun getSources(): List<Source>
    fun getSourceById(id: String): Source
    fun createSource(source: SourcePostDto): Source
    fun updateSource(id: String, source: SourcePostDto): Source
    fun deleteSource(id: String)
}
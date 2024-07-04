package lt.skafis.bankas.service

import lt.skafis.bankas.model.Source

interface SourceService {
    fun getSources(): List<Source>
    fun getSourceById(id: String): Source
    fun createSource(source: Source): Source
    fun updateSource(id: String, source: Source): Source
    fun deleteSource(id: String)
}
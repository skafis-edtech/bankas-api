package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.SourcePostDto
import lt.skafis.bankas.model.Source
import lt.skafis.bankas.repository.SourceRepository
import lt.skafis.bankas.service.SourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class SourceServiceImpl: SourceService {

    @Autowired
    private lateinit var sourceRepository: SourceRepository

    override fun getSources(): List<Source> {
        return sourceRepository.findAll()
            .sortedBy { it.lastModifiedOn }
    }

    override fun getSourceById(id: String): Source =
         sourceRepository.findById(id) ?: throw NotFoundException("Source with id $id not found")


    override fun createSource(source: SourcePostDto): Source =
         sourceRepository.create(
            Source(
                name = source.name,
                description = source.description,
                reviewStatus = source.reviewStatus,
                reviewHistory = source.reviewHistory,
                authorId = source.authorId,
                createdOn = source.createdOn,
                lastModifiedOn = source.lastModifiedOn
            )
        )


    override fun updateSource(id: String, source: SourcePostDto): Source {
        var sourceToUpdate = sourceRepository.findById(id) ?: throw NotFoundException("Source with id $id not found")
        sourceToUpdate = sourceToUpdate.copy(
            name = source.name,
            description = source.description,
            reviewStatus = source.reviewStatus,
            reviewHistory = source.reviewHistory,
            authorId = source.authorId,
            createdOn = source.createdOn,
            lastModifiedOn = source.lastModifiedOn
        )
        val success = sourceRepository.update(sourceToUpdate, id)
        return if (success) sourceToUpdate
        else throw Exception("Failed to update source with id $id")
    }

    override fun deleteSource(id: String) {
        val success = sourceRepository.delete(id)
        if (!success) throw Exception("Failed to delete source with id $id")
    }
}
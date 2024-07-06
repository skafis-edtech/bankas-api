package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.dto.SourcePostDto
import lt.skafis.bankas.model.Source
import lt.skafis.bankas.modelOld.Role
import lt.skafis.bankas.repository.SourceRepository
import lt.skafis.bankas.service.SourceService
import lt.skafis.bankas.service.UserService
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class SourceServiceImpl(
    private val sourceRepository: SourceRepository,
    private val userService: UserService
): SourceService {
    override fun getSources(): List<Source> {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        return sourceRepository.findAll()
    }

    override fun getSourceById(id: String): Source {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        return sourceRepository.findById(id) ?: throw NotFoundException("Source with id $id not found")
    }

    override fun createSource(source: SourcePostDto): Source {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        return sourceRepository.create(
            Source(
                name = source.name,
                description = source.description,
                reviewStatus = source.reviewStatus,
                reviewedBy = source.reviewedBy,
                reviewedOn = source.reviewedOn,
                reviewMessage = source.reviewMessage,
                author = source.author,
                createdOn = source.createdOn,
                lastModifiedOn = source.lastModifiedOn
            )
        )
    }

    override fun updateSource(id: String, source: SourcePostDto): Source {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        var sourceToUpdate = sourceRepository.findById(id) ?: throw NotFoundException("Source with id $id not found")
        sourceToUpdate = sourceToUpdate.copy(
            name = source.name,
            description = source.description,
            reviewStatus = source.reviewStatus,
            reviewedBy = source.reviewedBy,
            reviewedOn = source.reviewedOn,
            reviewMessage = source.reviewMessage,
            author = source.author,
            createdOn = source.createdOn,
            lastModifiedOn = source.lastModifiedOn
        )
        val success = sourceRepository.update(sourceToUpdate, id)
        return if (success) sourceToUpdate
        else throw Exception("Failed to update source with id $id")
    }

    override fun deleteSource(id: String) {
        userService.grantRoleAtLeast(Role.SUPER_ADMIN)
        val success = sourceRepository.delete(id)
        if (!success) throw Exception("Failed to delete source with id $id")
    }
}
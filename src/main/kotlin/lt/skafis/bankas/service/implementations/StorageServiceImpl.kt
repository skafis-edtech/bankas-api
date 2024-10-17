package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.repository.storage.StorageRepository
import lt.skafis.bankas.service.StorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StorageServiceImpl : StorageService {
    @Autowired
    private lateinit var storageRepository: StorageRepository

    override fun utilsGetImageSrc(imagePath: String): String =
        imagePath.let {
            if (
                it.startsWith("problems/") ||
                it.startsWith("answers/")
            ) {
                storageRepository.getImageUrl(it)
            } else if (it.isEmpty()) {
                ""
            } else {
                throw IllegalArgumentException("Invalid image path: $it")
            }
        }
}

package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.model.Category
import lt.skafis.bankas.repository.firestore.CategoryRepository
import lt.skafis.bankas.repository.realtimedb.CategoryRealtimeRepository
import lt.skafis.bankas.service.RealtimeService
import org.springframework.stereotype.Service

@Service
class RealtimeServiceImpl(
    private val categoryRealtimeRepository: CategoryRealtimeRepository,
    private val categoryRepository: CategoryRepository,
) : RealtimeService {
    override fun getAllCategories(): List<Category> = categoryRealtimeRepository.getCollectionCache().values.toList()

    override fun updateCategories() {
        val categories = categoryRepository.findAll()
        categoryRealtimeRepository.setCollectionValue(categories.associateBy { it.id })
    }
}

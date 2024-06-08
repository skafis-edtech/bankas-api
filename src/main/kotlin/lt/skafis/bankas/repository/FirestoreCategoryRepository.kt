package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.dto.CategoryViewDto
import org.springframework.stereotype.Repository

@Repository
class FirestoreCategoryRepository(private val firestore: Firestore) {

    private val collectionPath = "categories"

    fun createCategory(category: CategoryViewDto): String {
        val docRef = firestore.collection(collectionPath).document()
        val categoryWithId = category.copy(id = docRef.id)
        docRef.set(categoryWithId)
        return docRef.id
    }

    fun getCategoryById(id: String): CategoryViewDto? {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            val categoryDto = docSnapshot.toObject(CategoryViewDto::class.java)
            categoryDto?.let { CategoryViewDto(it.id, it.name, it.description, it.createdOn, it.createdBy) }
        } else {
            null
        }
    }

    fun updateCategory(category: CategoryViewDto): Boolean {
        val docRef = firestore.collection(collectionPath).document(category.id)
        return try {
            docRef.set(category.copy(id = category.id)).get()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteCategory(id: String): Boolean {
        val docRef = firestore.collection(collectionPath).document(id)
        return try {
            docRef.delete().get()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getAllCategories(): List<CategoryViewDto> {
        val collection = firestore.collection(collectionPath).get().get()
        return collection.documents.mapNotNull {
            val categoryDto = it.toObject(CategoryViewDto::class.java)
            categoryDto.let { dto -> CategoryViewDto( it.id, dto.name, dto.description, dto.createdOn, dto.createdBy) }
        }
    }
}

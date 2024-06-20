package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Category
import org.springframework.stereotype.Repository

@Repository
class FirestoreCategoryRepository(private val firestore: Firestore) {

    private val collectionPath = "categories"

    fun createCategoryWithSpecifiedId(category: Category): String {
        val docRef = firestore.collection(collectionPath).document(category.id)
        docRef.set(category)
        return docRef.id
    }

    fun getCategoryById(id: String): Category? {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            docSnapshot.toObject(Category::class.java)
        } else {
            null
        }
    }

    fun updateCategory(category: Category): Boolean {
        val docRef = firestore.collection(collectionPath).document(category.id)
        return try {
            docRef.set(category).get()
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

    fun getAllCategories(): List<Category> {
        val collection = firestore.collection(collectionPath).get().get()
        return collection.documents.mapNotNull {
            it.toObject(Category::class.java)
        }
    }

    fun countDocuments(): Long {
        val collectionRef = firestore.collection(collectionPath)
        val countQuery = collectionRef.count()
        val countQuerySnapshot = countQuery.get().get()
        return countQuerySnapshot.count
    }

    fun getCategoriesByAuthor(username: String): List<Category> {
        val collection = firestore.collection(collectionPath)
            .whereEqualTo("author", username)
            .get()
            .get()
        return collection.documents.mapNotNull {
            it.toObject(Category::class.java)
        }
    }
}

package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.UnderReviewCategory
import org.springframework.stereotype.Repository

@Repository
class FirestoreUnderReviewCategoryRepository(private val firestore: Firestore) {

    private val collectionPath = "underReviewCategories"

    //Input: UnderReviewCategory with unnecessary id field
    //Output: id of the created category
    fun createCategory(category: UnderReviewCategory): String {
        val docRef = firestore.collection(collectionPath).document()
        val categoryWithId = category.copy(id = docRef.id)
        docRef.set(categoryWithId)
        return docRef.id
    }

    fun getCategoryById(id: String): UnderReviewCategory? {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            docSnapshot.toObject(UnderReviewCategory::class.java)
        } else {
            null
        }
    }

    fun updateCategory(category: UnderReviewCategory): Boolean {
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

    fun getAllCategories(): List<UnderReviewCategory> {
        val collection = firestore.collection(collectionPath).get().get()
        return collection.documents.mapNotNull {
            it.toObject(UnderReviewCategory::class.java)
        }
    }

    fun countDocuments(): Long {
        val collectionRef = firestore.collection(collectionPath)
        val countQuery = collectionRef.count()
        val countQuerySnapshot = countQuery.get().get()
        return countQuerySnapshot.count
    }

    fun getCategoriesByAuthor(author: String): List<UnderReviewCategory> {
        val querySnapshot = firestore.collection(collectionPath)
            .whereEqualTo("author", author)
            .get()
            .get()

        return querySnapshot.documents.mapNotNull {
            it.toObject(UnderReviewCategory::class.java)
        }
    }
}

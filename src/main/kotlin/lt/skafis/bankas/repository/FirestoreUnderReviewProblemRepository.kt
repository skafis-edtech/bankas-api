package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.UnderReviewProblem
import org.springframework.stereotype.Repository

@Repository
class FirestoreUnderReviewProblemRepository(private val firestore: Firestore) {

    private val collectionPath = "underReviewProblems"

    fun genNewProblemId(): String {
        return firestore.collection(collectionPath).document().id
    }

    fun createProblemWithGivenId(problem: UnderReviewProblem): String {
        val docRef = firestore.collection(collectionPath).document(problem.id)
        docRef.set(problem)
        return docRef.id
    }

    fun getProblemById(id: String): UnderReviewProblem? {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            val problemDto = docSnapshot.toObject(UnderReviewProblem::class.java)
            problemDto?.copy(id = docSnapshot.id)
        } else {
            null
        }
    }

    fun updateProblem(problem: UnderReviewProblem): Boolean {
        val docRef = firestore.collection(collectionPath).document(problem.id)
        return try {
            docRef.set(problem.copy(id = problem.id)).get()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteProblem(id: String): Boolean {
        val docRef = firestore.collection(collectionPath).document(id)
        return try {
            docRef.delete().get()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getAllProblems(): List<UnderReviewProblem> {
        val collection = firestore.collection(collectionPath).get().get()
        return collection.documents.mapNotNull {
            it.toObject(UnderReviewProblem::class.java)
        }
    }

    fun countDocuments(): Long {
        val collectionRef = firestore.collection(collectionPath)
        val countQuery = collectionRef.count()
        val countQuerySnapshot = countQuery.get().get()
        return countQuerySnapshot.count
    }

    fun getProblemsByCategoryId(categoryId: String): List<UnderReviewProblem> {
        val querySnapshot = firestore.collection(collectionPath)
            .whereEqualTo("categoryId", categoryId)
            .get()
            .get()

        return querySnapshot.documents.mapNotNull {
            val problemDto = it.toObject(UnderReviewProblem::class.java)
            problemDto.copy(id = it.id)
        }
    }

    fun getProblemsByAuthor(author: String): List<UnderReviewProblem> {
        val querySnapshot = firestore.collection(collectionPath)
            .whereEqualTo("author", author)
            .get()
            .get()

        return querySnapshot.documents.mapNotNull {
            val problemDto = it.toObject(UnderReviewProblem::class.java)
            problemDto.copy(id = it.id)
        }
    }
}

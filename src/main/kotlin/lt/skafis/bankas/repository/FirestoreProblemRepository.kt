package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Problem
import org.springframework.stereotype.Repository

@Repository
class FirestoreProblemRepository(private val firestore: Firestore) {

    private val collectionPath = "problems"

    fun createProblemWithSpecifiedId(problem: Problem): String {
        val docRef = firestore.collection(collectionPath).document()
        val problemWithId = problem.copy(id = docRef.id)
        docRef.set(problemWithId)
        return docRef.id
    }

    fun getProblemById(id: String): Problem? {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            docSnapshot.toObject(Problem::class.java)
        } else {
            null
        }
    }

    fun getProblemBySkfCode(skfCode: String): Problem? {
        val querySnapshot = firestore.collection(collectionPath)
            .whereEqualTo("skfCode", skfCode)
            .get()
            .get()

        return querySnapshot.documents.firstOrNull()?.toObject(Problem::class.java)
    }

    fun updateProblem(problem: Problem): Boolean {
        val docRef = firestore.collection(collectionPath).document(problem.id)
        return try {
            docRef.set(problem).get()
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

    fun getAllProblems(): List<Problem> {
        val collection = firestore.collection(collectionPath).get().get()
        return collection.documents.mapNotNull {
            it.toObject(Problem::class.java)
        }
    }

    fun countDocuments(): Long {
        val collectionRef = firestore.collection(collectionPath)
        val countQuery = collectionRef.count()
        val countQuerySnapshot = countQuery.get().get()
        return countQuerySnapshot.count
    }

    fun getProblemsByCategoryId(categoryId: String): List<Problem> {
        val querySnapshot = firestore.collection(collectionPath)
            .whereEqualTo("categoryId", categoryId)
            .get()
            .get()

        return querySnapshot.documents.mapNotNull {
            it.toObject(Problem::class.java)
        }
    }

    fun getProblemsByAuthor(author: String): List<Problem> {
        val querySnapshot = firestore.collection(collectionPath)
            .whereEqualTo("author", author)
            .get()
            .get()

        return querySnapshot.documents.mapNotNull {
            it.toObject(Problem::class.java)
        }
    }
}

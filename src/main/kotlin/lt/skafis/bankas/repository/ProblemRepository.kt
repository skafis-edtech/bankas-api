package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Problem
import org.springframework.stereotype.Repository

@Repository
class ProblemRepository(private val firestore: Firestore) : FirestoreCrudRepository<Problem>(firestore, Problem::class.java) {
    override val collectionPath = "problems"

    fun getBySourceId(sourceId: String): List<Problem> {
        return firestore.collection(collectionPath)
            .whereEqualTo("sourceId", sourceId)
            .get()
            .get()
            .documents
            .map { it.toObject(Problem::class.java) }
    }

    fun getByCategoryId(categoryId: String): List<Problem> {
        return firestore.collection(collectionPath)
            .whereEqualTo("categoryId", categoryId)
            .get()
            .get()
            .documents
            .map { it.toObject(Problem::class.java) }
    }

    fun getBySkfCode(skfCode: String): Problem {
        return firestore.collection(collectionPath)
            .whereEqualTo("skfCode", skfCode)
            .get()
            .get()
            .documents
            .map { it.toObject(Problem::class.java) }
            .firstOrNull() ?: throw Exception("Problem with skfCode $skfCode not found")
    }

    fun countApproved(): Long {
        return firestore.collection(collectionPath)
            .whereEqualTo("isApproved", true)
            .get()
            .get()
            .documents
            .size.toLong()
    }
}
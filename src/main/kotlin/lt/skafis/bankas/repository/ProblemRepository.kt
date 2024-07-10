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
}
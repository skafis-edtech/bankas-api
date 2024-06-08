package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.DocumentSnapshot
import lt.skafis.bankas.model.ProblemMeta
import org.springframework.stereotype.Repository

@Repository
class FirestoreMetaRepository(private val firestore: Firestore) {
    private val basePath = "meta"
    private val documentPath = "$basePath/problemMeta"

    fun getProblemMeta(): ProblemMeta? {
        val docRef = firestore.document(documentPath)
        val docSnapshot: DocumentSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            docSnapshot.toObject(ProblemMeta::class.java)
        } else {
            null
        }
    }

    fun updateProblemMeta(problemMeta: ProblemMeta): Boolean {
        val docRef = firestore.document(documentPath)
        return try {
            docRef.set(problemMeta).get()
            true
        } catch (e: Exception) {
            false
        }
    }
}

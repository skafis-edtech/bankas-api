package lt.skafis.bankas.repository.firestore

import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.ProblemMeta
import org.springframework.stereotype.Repository

@Repository
class MetaRepository(
    private val firestore: Firestore,
) {
    private val basePath = "meta"
    private val documentPath = "$basePath/problemMeta"

    // Cache to store the ProblemMeta
    @Volatile
    private var problemMetaCache: ProblemMeta? = null

    fun getProblemMeta(): ProblemMeta? {
        // Return from cache if available
        problemMetaCache?.let {
            return it
        }

        // If cache is empty, fetch from Firestore
        val docRef = firestore.document(documentPath)
        val docSnapshot: DocumentSnapshot = docRef.get().get()
        val problemMeta =
            if (docSnapshot.exists()) {
                docSnapshot.toObject(ProblemMeta::class.java)
            } else {
                null
            }

        // Populate cache
        problemMetaCache = problemMeta

        return problemMeta
    }

    fun updateProblemMeta(problemMeta: ProblemMeta): Boolean {
        val docRef = firestore.document(documentPath)
        return try {
            docRef.set(problemMeta).get()
            // Update cache
            problemMetaCache = problemMeta
            true
        } catch (e: Exception) {
            false
        }
    }

    // Optional: Method to clear the cache if needed
    fun clearCache() {
        problemMetaCache = null
    }
}

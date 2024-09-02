package lt.skafis.bankas.repository.firestore
import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Identifiable
import java.util.concurrent.ConcurrentHashMap

abstract class FirestoreCrudRepository<T : Identifiable>(
    private val firestore: Firestore,
    private val clazz: Class<T>,
) {
    abstract val collectionPath: String
    protected val collectionCache = ConcurrentHashMap<String, T>()

    fun create(document: T): T {
        val docRef = firestore.collection(collectionPath).document()
        document.id = docRef.id
        docRef.set(document).get()
        val savedDocument = docRef.get().get().toObject(clazz)!!
        collectionCache[savedDocument.id] = savedDocument
        return savedDocument
    }

    fun findById(id: String): T? {
        collectionCache[id]?.let {
            return it
        }

        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        val document = if (docSnapshot.exists()) docSnapshot.toObject(clazz) else null
        if (document != null) {
            collectionCache[id] = document
        }
        return document
    }

    fun update(
        document: T,
        id: String,
    ): Boolean {
        val docRef = firestore.collection(collectionPath).document(id)
        return try {
            docRef.set(document).get()
            collectionCache[id] = document
            true
        } catch (e: Exception) {
            false
        }
    }

    fun delete(id: String): Boolean {
        val docRef = firestore.collection(collectionPath).document(id)
        return try {
            docRef.delete().get()
            collectionCache.remove(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun findAll(): List<T> {
        // Depending on your use case, you might cache the whole collection,
        // or decide not to cache the result of this query.
        val collection = firestore.collection(collectionPath).get().get()
        val documents = collection.documents.mapNotNull { it.toObject(clazz) }
        documents.forEach { collectionCache[it.id] = it }
        return documents
    }

    fun countDocuments(): Long {
        val collectionRef = firestore.collection(collectionPath)
        val countQuery = collectionRef.count()
        val countQuerySnapshot = countQuery.get().get()
        return countQuerySnapshot.count
    }

    fun clearCache() {
        collectionCache.clear()
    }
}

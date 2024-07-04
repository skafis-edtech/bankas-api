package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore

abstract class FirestoreCrudRepository<T : Any>(private val firestore: Firestore, private val clazz: Class<T>) {

    abstract val collectionPath: String

    fun create(document: T): T {
        val docRef = firestore.collection(collectionPath).document()
        docRef.set(document)
        return docRef.get().get().toObject(clazz)!!
    }

    fun findById(id: String): T? {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) docSnapshot.toObject(clazz) else null
    }

    fun update(document: T, id: String): Boolean {
        val docRef = firestore.collection(collectionPath).document(id)
        return try {
            docRef.set(document).get()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun delete(id: String): Boolean {
        val docRef = firestore.collection(collectionPath).document(id)
        return try {
            docRef.delete().get()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun findAll(): List<T> {
        val collection = firestore.collection(collectionPath).get().get()
        return collection.documents.mapNotNull { it.toObject(clazz) }
    }

    fun countDocuments(): Long {
        val collectionRef = firestore.collection(collectionPath)
        val countQuery = collectionRef.count()
        val countQuerySnapshot = countQuery.get().get()
        return countQuerySnapshot.count
    }
}

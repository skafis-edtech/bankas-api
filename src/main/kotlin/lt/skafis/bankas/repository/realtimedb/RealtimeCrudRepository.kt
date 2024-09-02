package lt.skafis.bankas.repository.realtimedb

import com.google.firebase.database.DatabaseReference
import lt.skafis.bankas.model.Identifiable
import java.util.concurrent.ConcurrentHashMap

abstract class RealtimeCrudRepository<T : Identifiable>(
    private val databaseReference: DatabaseReference,
    private val clazz: Class<T>,
) {
    abstract val collectionPath: String
    private val collectionCache = ConcurrentHashMap<String, T>()

    fun getCollectionCache(): Map<String, T> = collectionCache

    fun setCollectionCache(collection: Map<String, T>) {
        collectionCache.clear()
        collectionCache.putAll(collection)
    }

    fun setCollectionValue(collection: Map<String, T>) {
        databaseReference.child(collectionPath).setValueAsync(collection)
    }
}

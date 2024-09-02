package lt.skafis.bankas.repository.firestore
import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.User
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class UserRepository(
    private val firestore: Firestore,
) {
    private val collectionPath = "users"

    // Caches
    private val userIdCache = ConcurrentHashMap<String, User?>()
    private val usernameCache = ConcurrentHashMap<String, User?>()

    fun getUserById(id: String): User? =
        userIdCache.computeIfAbsent(id) {
            val docRef = firestore.collection(collectionPath).document(id)
            val docSnapshot = docRef.get().get()
            if (docSnapshot.exists()) {
                docSnapshot.toObject(User::class.java)
            } else {
                null
            }
        }

    fun getUserByUsername(username: String): User? =
        usernameCache.computeIfAbsent(username) {
            val query =
                firestore
                    .collection(collectionPath)
                    .whereEqualTo("username", username)
                    .get()
                    .get()
            query.documents.firstOrNull()?.toObject(User::class.java)
        }

    fun updateUserBio(
        id: String,
        bio: String,
    ): Boolean {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            docRef.update("bio", bio)
            // Update the cache with the new bio information
            userIdCache.computeIfPresent(id) { _, user ->
                user.apply { this.bio = bio }
            }
            true
        } else {
            false
        }
    }

    fun clearCaches() {
        userIdCache.clear()
        usernameCache.clear()
    }
}

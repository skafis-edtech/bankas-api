package lt.skafis.bankas.repository.firestore
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
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
    private val emailCache = ConcurrentHashMap<String, User?>()

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

    fun getUserByEmail(email: String): User? =
        emailCache.computeIfAbsent(email) {
            val query =
                firestore
                    .collection(collectionPath)
                    .whereEqualTo("email", email)
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

    fun registerUser(user: User): String {
        val docRef = firestore.collection(collectionPath).document(user.id)
        docRef.set(user, SetOptions.merge()).get() // Save the user to Firestore
        userIdCache[user.id] = user // Update caches
        usernameCache[user.username] = user
        emailCache[user.email] = user
        return user.id
    }

    fun getAllUsers(): List<User> {
        val collection = firestore.collection(collectionPath).get().get()
        val documents = collection.documents.mapNotNull { it.toObject(User::class.java) }
        return documents
    }

    fun clearCaches() {
        userIdCache.clear()
        usernameCache.clear()
    }
}

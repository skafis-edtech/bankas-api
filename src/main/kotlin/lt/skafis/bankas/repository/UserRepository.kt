package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.User
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val firestore: Firestore) {

    private val collectionPath = "users"

    fun getUserById(id: String): User? {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            docSnapshot.toObject(User::class.java)
        } else {
            null
        }
    }

    fun getUserByUsername(username: String): User? {
        val query = firestore.collection(collectionPath).whereEqualTo("username", username).get().get()
        return query.documents.firstOrNull()?.toObject(User::class.java)
    }

    fun updateUserBio(id: String, bio: String): Boolean {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            docRef.update("bio", bio)
            true
        } else {
            false
        }
    }

    fun getByUsername(username: String): User? {
        val query = firestore.collection(collectionPath).whereEqualTo("username", username).get().get()
        return query.documents.firstOrNull()?.toObject(User::class.java)
    }
}
package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.dto.UserViewDto
import org.springframework.stereotype.Repository

@Repository
class FirestoreUserRepository(private val firestore: Firestore) {

    private val collectionPath = "users"

    fun getUserById(id: String): UserViewDto? {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            val userDto = docSnapshot.toObject(UserViewDto::class.java)
            userDto?.let { UserViewDto(it.id, it.email, it.username, it.role) }
        } else {
            null
        }
    }
}
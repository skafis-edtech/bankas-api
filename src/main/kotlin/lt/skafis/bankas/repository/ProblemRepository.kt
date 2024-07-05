package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Problem
import org.springframework.stereotype.Repository

@Repository
class ProblemRepository(firestore: Firestore) : FirestoreCrudRepository<Problem>(firestore, Problem::class.java) {
    override val collectionPath = "problems"
}
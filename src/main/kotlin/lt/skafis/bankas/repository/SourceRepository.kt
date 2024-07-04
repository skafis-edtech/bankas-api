package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Source
import org.springframework.stereotype.Repository

@Repository
class SourceRepository(private val firestore: Firestore): FirestoreCrudRepository<Source>(firestore, Source::class.java) {
    override val collectionPath = "sources"
}

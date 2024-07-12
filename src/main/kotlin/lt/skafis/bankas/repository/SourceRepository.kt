package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Source
import org.springframework.stereotype.Repository

@Repository
class SourceRepository(private val firestore: Firestore): FirestoreCrudRepository<Source>(firestore, Source::class.java) {
    override val collectionPath = "sources"

    fun getByAuthor(author: String): List<Source> {
        return firestore.collection(collectionPath)
            .whereEqualTo("author", author)
            .get()
            .get()
            .documents
            .map { it.toObject(Source::class.java) }
    }

    fun getByAuthorSorted(author: String): List<Source> {
        return firestore.collection(collectionPath)
            .whereEqualTo("author", author)
            .whereNotEqualTo("categoryId", "")
            .get()
            .get()
            .documents
            .map { it.toObject(Source::class.java) }
    }

    fun getByNotAuthorSorted(author: String): List<Source> {
        return firestore.collection(collectionPath)
            .whereNotEqualTo("author", author)
            .whereNotEqualTo("categoryId", "")
            .get()
            .get()
            .documents
            .map { it.toObject(Source::class.java) }
    }

    fun getByAuthorUnsorted(author: String): List<Source> {
        return firestore.collection(collectionPath)
            .whereEqualTo("author", author)
            .whereEqualTo("categoryId", "")
            .get()
            .get()
            .documents
            .map { it.toObject(Source::class.java) }
    }

    fun getByNotAuthorUnsorted(author: String): List<Source> {
        return firestore.collection(collectionPath)
            .whereNotEqualTo("author", author)
            .whereEqualTo("categoryId", "")
            .get()
            .get()
            .documents
            .map { it.toObject(Source::class.java) }
    }

}

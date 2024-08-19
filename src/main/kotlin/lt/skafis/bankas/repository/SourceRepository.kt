package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.ReviewStatus
import lt.skafis.bankas.model.Source
import org.springframework.stereotype.Repository
import java.text.Normalizer

@Repository
class SourceRepository(
    private val firestore: Firestore,
) : FirestoreCrudRepository<Source>(firestore, Source::class.java) {
    override val collectionPath = "sources"

    fun getByAuthor(author: String): List<Source> =
        firestore
            .collection(collectionPath)
            .whereEqualTo("authorId", author)
            .get()
            .get()
            .documents
            .map { it.toObject(Source::class.java) }

    fun getByNotAuthor(author: String): List<Source> =
        firestore
            .collection(collectionPath)
            .whereNotEqualTo("authorId", author)
            .get()
            .get()
            .documents
            .map { it.toObject(Source::class.java) }

    fun getApprovedSearchPageable(
        search: String,
        limit: Int,
        offset: Long,
    ): List<Source> {
        val collectionRef = firestore.collection(collectionPath)

        val query = collectionRef.orderBy("lastModifiedOn").get().get()
        val documents = query.documents

        // If search is not null, filter the documents based on the search criteria
        val filteredDocuments =
            if (!search.isEmpty()) {
                val normalizedSearch = normalizeString(search)
                documents.filter { document ->
                    val source = document.toObject(Source::class.java)
                    source.name.let { normalizeString(it).contains(normalizedSearch) } && source.reviewStatus == ReviewStatus.APPROVED
                }
            } else {
                documents.filter { document ->
                    val source = document.toObject(Source::class.java)
                    source.reviewStatus == ReviewStatus.APPROVED
                }
            }

        // Apply pagination
        val pagedDocuments = filteredDocuments.drop(offset.toInt()).take(limit.toInt())

        return pagedDocuments.mapNotNull { it.toObject(Source::class.java) }
    }

    fun normalizeString(input: String): String =
        Normalizer
            .normalize(input, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .replace("[čČ]".toRegex(), "c")
            .replace("[šŠ]".toRegex(), "s")
            .replace("[žŽ]".toRegex(), "z")
            .replace("[ąĄ]".toRegex(), "a")
            .replace("[ęĘ]".toRegex(), "e")
            .replace("[ėĖ]".toRegex(), "e")
            .replace("[įĮ]".toRegex(), "i")
            .replace("[ųŲ]".toRegex(), "u")
            .replace("[ūŪ]".toRegex(), "u")
            .lowercase()
}

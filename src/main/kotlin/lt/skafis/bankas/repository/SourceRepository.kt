package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
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

        val query = collectionRef.orderBy("lastModifiedOn", Query.Direction.DESCENDING).get().get()
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

    fun getPendingSearchPageable(
        search: String,
        limit: Int,
        offset: Long,
    ): List<Source> {
        val collectionRef = firestore.collection(collectionPath)

        val query = collectionRef.orderBy("lastModifiedOn", Query.Direction.DESCENDING).get().get()
        val documents = query.documents

        // Normalize the search term if it's provided
        val normalizedSearch = normalizeString(search)

        // Filter the documents based on the search criteria
        val filteredDocuments =
            documents.filter { document ->
                val source = document.toObject(Source::class.java)
                val matchesSearch = search.isEmpty() || normalizeString(source.name).contains(normalizedSearch)
                matchesSearch && source.reviewStatus == ReviewStatus.PENDING
            }

        // Sort the filtered documents into two parts:
        // 1. Names not containing "(DAR TVARKOMA)"
        // 2. Names containing "(DAR TVARKOMA)"
        val sortedDocuments =
            filteredDocuments.sortedWith(
                compareBy(
                    { it.toObject(Source::class.java).name.contains("(DAR TVARKOMA)") },
                    { it.toObject(Source::class.java).lastModifiedOn },
                ),
            )

// Apply pagination
        val pagedDocuments = sortedDocuments.drop(offset.toInt()).take(limit.toInt())

        return pagedDocuments.mapNotNull { it.toObject(Source::class.java) }
    }

    fun getByAuthorSearchPageable(
        userId: String,
        search: String,
        limit: Int,
        offset: Long,
        isApproved: Boolean = false,
    ): List<Source> {
        val collectionRef = firestore.collection(collectionPath)

        val query = collectionRef.orderBy("lastModifiedOn", Query.Direction.DESCENDING).get().get()
        val documents = query.documents.mapNotNull { it.toObject(Source::class.java) }

// Group by review status
        val groupedDocuments = documents.groupBy { it.reviewStatus }

// Define the order of statuses
        val statusOrder = listOf(ReviewStatus.REJECTED, ReviewStatus.PENDING, ReviewStatus.APPROVED)

// Sort and flatten the grouped documents
        val sortedDocuments =
            statusOrder.flatMap { status ->
                groupedDocuments[status] ?: emptyList()
            }
        val filteredDocuments =
            if (search.isNotEmpty()) {
                val normalizedSearch = normalizeString(search)
                sortedDocuments.filter { source ->
                    normalizeString(source.name).contains(normalizedSearch) &&
                        source.authorId == userId &&
                        (!isApproved || source.reviewStatus == ReviewStatus.APPROVED)
                }
            } else {
                sortedDocuments.filter { source ->
                    source.authorId == userId &&
                        (!isApproved || source.reviewStatus == ReviewStatus.APPROVED)
                }
            }

// Apply pagination
        val pagedDocuments = filteredDocuments.drop(offset.toInt()).take(limit.toInt())

        return pagedDocuments
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

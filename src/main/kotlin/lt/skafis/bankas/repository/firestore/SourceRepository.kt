package lt.skafis.bankas.repository.firestore
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import lt.skafis.bankas.model.ReviewStatus
import lt.skafis.bankas.model.Source
import lt.skafis.bankas.model.Visibility
import org.springframework.stereotype.Repository
import java.text.Normalizer
import java.util.concurrent.ConcurrentHashMap

@Repository
class SourceRepository(
    private val firestore: Firestore,
) : FirestoreCrudRepository<Source>(firestore, Source::class.java) {
    override val collectionPath = "sources"

    // Caches
    private val authorCache = ConcurrentHashMap<String, List<Source>>()
    private val approvedCache = ConcurrentHashMap<String, List<Source>>()
    private val pendingCache = ConcurrentHashMap<String, List<Source>>()

    fun getApprovedSearchPageable(
        search: String,
        limit: Int,
        offset: Long,
    ): List<Source> {
        val cacheKey = "approved:$search:$limit:$offset"
        return approvedCache.computeIfAbsent(cacheKey) {
            val collectionRef = firestore.collection(collectionPath)
            val query = collectionRef.orderBy("lastModifiedOn", Query.Direction.DESCENDING).get().get()
            val documents = query.documents

            // Filter documents based on the search criteria and approval status
            val filteredDocuments =
                if (search.isNotEmpty()) {
                    val normalizedSearch = normalizeString(search)
                    documents.filter { document ->
                        val source = document.toObject(Source::class.java)
                        normalizeString(source.name).contains(normalizedSearch) && source.reviewStatus == ReviewStatus.APPROVED
                    }
                } else {
                    documents.filter { document ->
                        val source = document.toObject(Source::class.java)
                        source.reviewStatus == ReviewStatus.APPROVED
                    }
                }

            // Apply pagination
            val pagedDocuments = filteredDocuments.drop(offset.toInt()).take(limit)

            pagedDocuments.mapNotNull { it.toObject(Source::class.java) }
        }
    }

    fun getPendingSearchPageable(
        search: String,
        limit: Int,
        offset: Long,
    ): List<Source> {
        val cacheKey = "pending:$search:$limit:$offset"
        return pendingCache.computeIfAbsent(cacheKey) {
            val collectionRef = firestore.collection(collectionPath)
            val query = collectionRef.orderBy("lastModifiedOn", Query.Direction.DESCENDING).get().get()
            val documents = query.documents

            // Normalize the search term if it's provided
            val normalizedSearch = normalizeString(search)

            // Filter and sort the documents based on the search criteria
            val filteredDocuments =
                documents.filter { document ->
                    val source = document.toObject(Source::class.java)
                    val matchesSearch = search.isEmpty() || normalizeString(source.name).contains(normalizedSearch)
                    matchesSearch && source.reviewStatus == ReviewStatus.PENDING && source.visibility == Visibility.PUBLIC
                }

            // Sort the filtered documents into two parts:
            val sortedDocuments =
                filteredDocuments.sortedWith(
                    compareBy(
                        { it.toObject(Source::class.java).name.contains("(DAR TVARKOMA)") },
                        { it.toObject(Source::class.java).lastModifiedOn },
                    ),
                )

            // Apply pagination
            val pagedDocuments = sortedDocuments.drop(offset.toInt()).take(limit)

            pagedDocuments.mapNotNull { it.toObject(Source::class.java) }
        }
    }

    fun getByAuthorSearchPageable(
        userId: String,
        search: String,
        limit: Int,
        offset: Long,
        isApproved: Boolean = false,
    ): List<Source> {
        val cacheKey = "authorSearch:$userId:$search:$limit:$offset:$isApproved"
        return authorCache.computeIfAbsent(cacheKey) {
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
            val pagedDocuments = filteredDocuments.drop(offset.toInt()).take(limit)

            pagedDocuments
        }
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

    // Optional: Method to clear all caches
    fun clearAllCaches() {
        authorCache.clear()
        approvedCache.clear()
        pendingCache.clear()
    }

    override fun create(document: Source): Source {
        clearAllCaches()
        return super.create(document)
    }

    override fun update(
        document: Source,
        id: String,
    ): Boolean {
        clearAllCaches()
        return super.update(document, id)
    }

    override fun delete(id: String): Boolean {
        clearAllCaches()
        return super.delete(id)
    }
}

package lt.skafis.bankas.repository.firestore
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import lt.skafis.bankas.model.ReviewStatus
import lt.skafis.bankas.model.SortBy
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
    private val availableCache = ConcurrentHashMap<String, List<Source>>()
    private val pendingCache = ConcurrentHashMap<String, List<Source>>()

    fun getAvailableSources(
        search: String,
        limit: Int,
        offset: Long,
        sortBy: SortBy,
        userId: String,
    ): List<Source> {
        val cacheKey = "available:$search:$userId:$limit:$offset:$sortBy"
        return availableCache.computeIfAbsent(cacheKey) {
            val collectionRef = firestore.collection(collectionPath)
            var documents = emptyList<Source>()
            if (sortBy == SortBy.NEWEST) {
                val query = collectionRef.orderBy("lastModifiedOn", Query.Direction.DESCENDING).get().get()
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            } else if (sortBy == SortBy.OLDEST) {
                val query = collectionRef.orderBy("lastModifiedOn", Query.Direction.ASCENDING).get().get()
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            } else if (sortBy == SortBy.NAME_ASC) {
                val query = collectionRef.orderBy("name", Query.Direction.ASCENDING).get().get()
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            } else if (sortBy == SortBy.NAME_DESC) {
                val query = collectionRef.orderBy("name", Query.Direction.DESCENDING).get().get()
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            } else if (sortBy == SortBy.MOST_PROBLEMS) {
                val query = collectionRef.orderBy("problemCount", Query.Direction.DESCENDING).get().get()
                // TODO: Add a problemCount field in Firestore
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            } else if (sortBy == SortBy.LEAST_PROBLEMS) {
                // TODO: Add a problemCount field in Firestore
                val query = collectionRef.orderBy("problemCount", Query.Direction.ASCENDING).get().get()
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            }

            val filteredDocuments =
                if (search.isNotEmpty()) {
                    val normalizedSearch = normalizeString(search)
                    documents.filter { source ->
                        (
                            normalizeString(source.name).contains(normalizedSearch) ||
                                normalizeString(source.description).contains(normalizedSearch)
                        ) &&
                            (
                                source.reviewStatus == ReviewStatus.APPROVED ||
                                    source.authorId == userId
                            )
                    }
                } else {
                    documents.filter { source ->
                        source.reviewStatus == ReviewStatus.APPROVED ||
                            source.authorId == userId
                    }
                }

            val pagedDocuments = filteredDocuments.drop(offset.toInt()).take(limit)

            pagedDocuments
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

            // Apply pagination
            val pagedDocuments = filteredDocuments.drop(offset.toInt()).take(limit)

            pagedDocuments.mapNotNull { it.toObject(Source::class.java) }
        }
    }

    fun getByAuthorSearchPageable(
        userId: String,
        search: String,
        limit: Int,
        offset: Long,
        isApproved: Boolean = false,
        sortBy: SortBy = SortBy.NEWEST,
    ): List<Source> {
        val cacheKey = "authorSearch:$userId:$search:$limit:$offset:$isApproved:$sortBy"
        return authorCache.computeIfAbsent(cacheKey) {
            val collectionRef = firestore.collection(collectionPath)
            var documents = emptyList<Source>()
            if (sortBy == SortBy.NEWEST) {
                val query = collectionRef.orderBy("lastModifiedOn", Query.Direction.DESCENDING).get().get()
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            } else if (sortBy == SortBy.OLDEST) {
                val query = collectionRef.orderBy("lastModifiedOn", Query.Direction.ASCENDING).get().get()
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            } else if (sortBy == SortBy.NAME_ASC) {
                val query = collectionRef.orderBy("name", Query.Direction.ASCENDING).get().get()
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            } else if (sortBy == SortBy.NAME_DESC) {
                val query = collectionRef.orderBy("name", Query.Direction.DESCENDING).get().get()
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            } else if (sortBy == SortBy.MOST_PROBLEMS) {
                val query = collectionRef.orderBy("problemCount", Query.Direction.DESCENDING).get().get()
                // TODO: Add a problemCount field in FIrestore
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            } else if (sortBy == SortBy.LEAST_PROBLEMS) {
                // TODO: Add a problemCount field in FIrestore
                val query = collectionRef.orderBy("problemCount", Query.Direction.ASCENDING).get().get()
                documents = query.documents.mapNotNull { it.toObject(Source::class.java) }
            }

            val filteredDocuments =
                if (search.isNotEmpty()) {
                    val normalizedSearch = normalizeString(search)
                    documents.filter { source ->
                        (
                            normalizeString(source.name).contains(normalizedSearch) ||
                                normalizeString(source.description).contains(normalizedSearch)
                        ) &&
                            source.authorId == userId &&
                            (!isApproved || source.reviewStatus == ReviewStatus.APPROVED)
                    }
                } else {
                    documents.filter { source ->
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
        availableCache.clear()
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

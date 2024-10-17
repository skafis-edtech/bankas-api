package lt.skafis.bankas.repository.firestore
import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Problem
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class ProblemRepository(
    private val firestore: Firestore,
) : FirestoreCrudRepository<Problem>(firestore, Problem::class.java) {
    override val collectionPath = "problems"

    // Caches
    private val sourceIdCache = ConcurrentHashMap<String, List<Problem>>()
    private val skfCodeCache = ConcurrentHashMap<String, Problem>()
    private val categoryIdCache = ConcurrentHashMap<String, List<Problem>>()
    private val approvedCache = ConcurrentHashMap<String, List<Problem>>()

    fun getBySourceId(sourceId: String): List<Problem> =
        sourceIdCache.computeIfAbsent(sourceId) {
            firestore
                .collection(collectionPath)
                .whereEqualTo("sourceId", sourceId)
                .get()
                .get()
                .documents
                .mapNotNull { it.toObject(Problem::class.java) }
        }

    fun getByCategoryId(categoryId: String): List<Problem> =
        categoryIdCache.computeIfAbsent(categoryId) {
            firestore
                .collection(collectionPath)
                .whereArrayContains("categories", categoryId)
                .get()
                .get()
                .documents
                .mapNotNull { it.toObject(Problem::class.java) }
        }

    fun getBySkfCode(skfCode: String): Problem =
        skfCodeCache.computeIfAbsent(skfCode) {
            firestore
                .collection(collectionPath)
                .whereEqualTo("skfCode", skfCode)
                .get()
                .get()
                .documents
                .mapNotNull { it.toObject(Problem::class.java) }
                .firstOrNull() ?: Problem()
        }

    fun countApproved(): Long =
        approvedCache
            .computeIfAbsent("approved") {
                firestore
                    .collection(collectionPath)
                    .whereEqualTo("isApproved", true)
                    .get()
                    .get()
                    .documents
                    .mapNotNull { it.toObject(Problem::class.java) }
            }.size
            .toLong()

    fun countApprovedByCategoryId(categoryId: String): Long {
        val cacheKey = "approved:$categoryId"
        return categoryIdCache
            .computeIfAbsent(cacheKey) {
                firestore
                    .collection(collectionPath)
                    .whereEqualTo("isApproved", true)
                    .whereArrayContains("categories", categoryId)
                    .get()
                    .get()
                    .documents
                    .mapNotNull { it.toObject(Problem::class.java) }
            }.size
            .toLong()
    }

    fun countUnsortedApproved(): Long =
        approvedCache
            .computeIfAbsent("unsortedApproved") {
                firestore
                    .collection(collectionPath)
                    .whereEqualTo("isApproved", true)
                    .whereEqualTo("categories", emptyList<String>())
                    .get()
                    .get()
                    .documents
                    .mapNotNull { it.toObject(Problem::class.java) }
            }.size
            .toLong()

    fun getUnsortedApprovedProblems(): List<Problem> =
        approvedCache.computeIfAbsent("unsortedApproved") {
            firestore
                .collection(collectionPath)
                .whereEqualTo("isApproved", true)
                .whereEqualTo("categories", emptyList<String>())
                .get()
                .get()
                .documents
                .mapNotNull { it.toObject(Problem::class.java) }
        }

    fun getBySourceIdPageable(
        sourceId: String,
        limit: Int,
        offset: Long,
    ): List<Problem> {
        val cacheKey = "pageable:$sourceId:$limit:$offset"
        return sourceIdCache.computeIfAbsent(cacheKey) {
            val collectionRef = firestore.collection(collectionPath)
            val query =
                collectionRef
                    .orderBy("sourceListNr")
                    .whereEqualTo("sourceId", sourceId)
                    .get()
                    .get()
            val documents = query.documents
            val pagedDocuments = documents.drop(offset.toInt()).take(limit)
            pagedDocuments.mapNotNull { it.toObject(Problem::class.java) }
        }
    }

    fun countBySource(sourceId: String): Long {
        val cacheKey = "count:$sourceId"
        return sourceIdCache
            .computeIfAbsent(cacheKey) {
                firestore
                    .collection(collectionPath)
                    .whereEqualTo("sourceId", sourceId)
                    .get()
                    .get()
                    .documents
                    .mapNotNull { it.toObject(Problem::class.java) }
            }.size
            .toLong()
    }

    // Optional: Method to clear all caches
    fun clearAllCaches() {
        sourceIdCache.clear()
        skfCodeCache.clear()
        categoryIdCache.clear()
        approvedCache.clear()
    }

    override fun create(document: Problem): Problem {
        val createdDocument = super.create(document)
        clearAllCaches() // Clear all caches after creating a document
        return createdDocument
    }

    override fun update(
        document: Problem,
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

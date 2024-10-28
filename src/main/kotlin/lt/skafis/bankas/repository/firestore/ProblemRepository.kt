package lt.skafis.bankas.repository.firestore
import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Problem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Repository
class ProblemRepository(
    private val firestore: Firestore,
) : FirestoreCrudRepository<Problem>(firestore, Problem::class.java) {
    @Autowired
    private lateinit var sourceRepository: SourceRepository

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

    fun getAvailableByCategoryId(
        categoryId: String,
        limit: Int,
        offset: Long,
        userId: String,
        seed: Long,
    ): List<Problem> {
        val cacheKey = "approved_or_owned:$categoryId:$userId"

        val problems =
            categoryIdCache.computeIfAbsent(cacheKey) {
                firestore
                    .collection(collectionPath)
                    .whereArrayContains("categories", categoryId)
                    .get()
                    .get()
                    .documents
                    .mapNotNull { it.toObject(Problem::class.java) }
                    .filter { it.isApproved || sourceRepository.findById(it.sourceId)!!.authorId == userId }
            }

        // Shuffle with seed
        val random = Random(seed)
        val shuffledProblems = problems.shuffled(random)

        // Apply pagination
        return shuffledProblems.drop(offset.toInt()).take(limit)
    }

    fun getAllAvailableByCategory(
        categoryId: String,
        userId: String,
    ): List<Problem> =
        firestore
            .collection(collectionPath)
            .whereArrayContains("categories", categoryId)
            .get()
            .get()
            .documents
            .mapNotNull { it.toObject(Problem::class.java) }
            .filter { it.isApproved || sourceRepository.findById(it.sourceId)!!.authorId == userId }

    fun getAvailableByCategoryAndSourceExceptList(
        categoryId: String,
        limit: Int,
        offset: Long,
        userId: String,
        sourceIds: List<String>,
        seed: Long,
    ): List<Problem> {
        val cacheKey = "approved_or_owned_except:$categoryId:$userId:${sourceIds.joinToString(",")}"
        val problems =
            categoryIdCache.computeIfAbsent(cacheKey) {
                firestore
                    .collection(collectionPath)
                    .whereArrayContains("categories", categoryId)
                    .get()
                    .get()
                    .documents
                    .mapNotNull { it.toObject(Problem::class.java) }
                    .filter { it.sourceId !in sourceIds }
                    .filter { it.isApproved || sourceRepository.findById(it.sourceId)!!.authorId == userId }
            }

        // Shuffle with seed
        val random = Random(seed)
        val shuffledProblems = problems.shuffled(random)

        // Apply pagination
        return shuffledProblems.drop(offset.toInt()).take(limit)
    }

    fun getAvailableByCategoryAndSourceList(
        categoryId: String,
        limit: Int,
        offset: Long,
        userId: String,
        sourceIds: List<String>,
        seed: Long,
    ): List<Problem> {
        val cacheKey = "approved_or_owned_only:$categoryId:$userId:${sourceIds.joinToString(",")}"
        val problems =
            categoryIdCache.computeIfAbsent(cacheKey) {
                firestore
                    .collection(collectionPath)
                    .whereArrayContains("categories", categoryId)
                    .get()
                    .get()
                    .documents
                    .mapNotNull { it.toObject(Problem::class.java) }
                    .filter { it.sourceId in sourceIds }
                    .filter { it.isApproved || sourceRepository.findById(it.sourceId)!!.authorId == userId }
            }
        // Shuffle with seed
        val random = Random(seed)
        val shuffledProblems = problems.shuffled(random)

        // Apply pagination
        return shuffledProblems.drop(offset.toInt()).take(limit)
    }

    fun getBySkfCode(skfCode: String): Problem =
        skfCodeCache.computeIfAbsent(skfCode) {
            firestore
                .collection(collectionPath)
                .whereEqualTo("skfCode", skfCode)
                .get()
                .get()
                .documents
                .firstNotNullOfOrNull { it.toObject(Problem::class.java) } ?: Problem()
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

    fun countAvailableByCategoryId(
        userId: String,
        categoryId: String,
    ): Long {
        val cacheKey = "count_all$categoryId:$userId"

        return categoryIdCache
            .computeIfAbsent(cacheKey) {
                firestore
                    .collection(collectionPath)
                    .whereArrayContains("categories", categoryId)
                    .get()
                    .get()
                    .documents
                    .mapNotNull { it.toObject(Problem::class.java) }
                    .filter { it.isApproved || sourceRepository.findById(it.sourceId)!!.authorId == userId }
            }.size
            .toLong()
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

    fun countAvailableByCategoryAndSourceList(
        userId: String,
        categoryId: String,
        sourceIds: List<String>,
    ): Long {
        val cacheKey = "count_only:$categoryId:${sourceIds.joinToString(",")}"
        return categoryIdCache
            .computeIfAbsent(cacheKey) {
                firestore
                    .collection(collectionPath)
                    .whereArrayContains("categories", categoryId)
                    .get()
                    .get()
                    .documents
                    .mapNotNull { it.toObject(Problem::class.java) }
                    .filter { it.sourceId in sourceIds }
                    .filter { it.isApproved || sourceRepository.findById(it.sourceId)!!.authorId == userId }
            }.size
            .toLong()
    }

    fun countAvailableByCategoryAndSourceExceptList(
        userId: String,
        categoryId: String,
        sourceIds: List<String>,
    ): Long {
        val cacheKey = "count_except:$categoryId:${sourceIds.joinToString(",")}"
        return categoryIdCache
            .computeIfAbsent(cacheKey) {
                firestore
                    .collection(collectionPath)
                    .whereArrayContains("categories", categoryId)
                    .get()
                    .get()
                    .documents
                    .mapNotNull { it.toObject(Problem::class.java) }
                    .filter { it.sourceId !in sourceIds }
                    .filter { it.isApproved || sourceRepository.findById(it.sourceId)!!.authorId == userId }
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

package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Problem
import org.springframework.stereotype.Repository

@Repository
class ProblemRepository(
    private val firestore: Firestore,
) : FirestoreCrudRepository<Problem>(firestore, Problem::class.java) {
    override val collectionPath = "problems"

    fun getBySourceId(sourceId: String): List<Problem> =
        firestore
            .collection(collectionPath)
            .whereEqualTo("sourceId", sourceId)
            .get()
            .get()
            .documents
            .map { it.toObject(Problem::class.java) }

    fun getBySourceSorted(sourceId: String): List<Problem> =
        firestore
            .collection(collectionPath)
            .whereEqualTo("sourceId", sourceId)
            .whereNotEqualTo("categories", emptyList<String>())
            .get()
            .get()
            .documents
            .map { it.toObject(Problem::class.java) }

    fun getBySourceUnsorted(sourceId: String): List<Problem> =
        firestore
            .collection(collectionPath)
            .whereEqualTo("sourceId", sourceId)
            .whereEqualTo("categories", emptyList<String>())
            .get()
            .get()
            .documents
            .map { it.toObject(Problem::class.java) }

    fun getByCategoryId(categoryId: String): List<Problem> =
        firestore
            .collection(collectionPath)
            .whereArrayContains("categories", categoryId)
            .get()
            .get()
            .documents
            .map { it.toObject(Problem::class.java) }

    fun getBySkfCode(skfCode: String): Problem =
        firestore
            .collection(collectionPath)
            .whereEqualTo("skfCode", skfCode)
            .get()
            .get()
            .documents
            .map { it.toObject(Problem::class.java) }
            .firstOrNull() ?: throw Exception("Problem with skfCode $skfCode not found")

    fun countApproved(): Long =
        firestore
            .collection(collectionPath)
            .whereEqualTo("isApproved", true)
            .get()
            .get()
            .documents
            .size
            .toLong()

    fun countApprovedByCategoryId(categoryId: String): Long =
        firestore
            .collection(collectionPath)
            .whereEqualTo("isApproved", true)
            .whereArrayContains("categories", categoryId)
            .get()
            .get()
            .documents
            .size
            .toLong()

    fun countUnsortedApproved(): Long =
        firestore
            .collection(collectionPath)
            .whereEqualTo("isApproved", true)
            .whereEqualTo("categories", emptyList<String>())
            .get()
            .get()
            .documents
            .size
            .toLong()

    fun getUnsortedApprovedProblems(): List<Problem> =
        firestore
            .collection(collectionPath)
            .whereEqualTo("isApproved", true)
            .whereEqualTo("categories", emptyList<String>())
            .get()
            .get()
            .documents
            .map { it.toObject(Problem::class.java) }

    fun getBySourceIdPageable(
        sourceId: String,
        limit: Int,
        offset: Long,
    ): List<Problem> {
        val collectionRef = firestore.collection(collectionPath)

        val query =
            collectionRef
                .whereEqualTo("sourceId", sourceId)
                .get()
                .get()
        val documents = query.documents
        val pagedDocuments = documents.drop(offset.toInt()).take(limit.toInt())

        return pagedDocuments.mapNotNull { it.toObject(Problem::class.java) }
    }

    fun countBySource(sourceId: String): Long =
        firestore
            .collection(collectionPath)
            .whereEqualTo("sourceId", sourceId)
            .get()
            .get()
            .documents
            .size
            .toLong()
}

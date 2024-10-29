package lt.skafis.bankas.repository.firestore

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.model.Visibility
import org.springframework.stereotype.Repository
import java.text.Normalizer
import java.util.concurrent.ConcurrentHashMap

@Repository
class CategoryRepository(
    private val firestore: Firestore,
) : FirestoreCrudRepository<Category>(firestore, Category::class.java) {
    override val collectionPath = "categories"

    private val collectionCache = ConcurrentHashMap<String, List<Category>>()

    fun getAvailableCategories(
        search: String,
        limit: Int,
        offset: Long,
        userId: String,
    ): List<Category> {
        val cacheKey = "pageable:$search:$limit:$offset:$userId"

        return collectionCache.computeIfAbsent(cacheKey) {
            val collectionRef = firestore.collection(collectionPath)
            val query = collectionRef.orderBy("name").get().get()
            val documents = query.documents

            val normalizedSearch = normalizeString(search)

            val filteredDocuments =
                documents.mapNotNull { document ->
                    val category = document.toObject(Category::class.java)
                    // Make sure to check if category is not null
                    if (
                        category.visibility == Visibility.PUBLIC ||
                        (category.visibility == Visibility.PRIVATE && category.ownerOfPrivateId == userId)
                    ) {
                        category
                    } else {
                        null
                    }
                }

            val searchFilteredDocuments =
                if (search.isNotEmpty()) {
                    filteredDocuments.filter { category ->
                        normalizeString(category.name).contains(normalizedSearch) ||
                            normalizeString(category.description).contains(normalizedSearch)
                    }
                } else {
                    filteredDocuments
                }

            val finalDocuments = searchFilteredDocuments.drop(offset.toInt()).take(limit)

            finalDocuments
        }
    }

    fun clearAllCaches() {
        collectionCache.clear()
    }

    override fun create(document: Category): Category {
        clearAllCaches()
        return super.create(document)
    }

    override fun update(
        document: Category,
        id: String,
    ): Boolean {
        clearAllCaches()
        return super.update(document, id)
    }

    override fun delete(id: String): Boolean {
        clearAllCaches()
        return super.delete(id)
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

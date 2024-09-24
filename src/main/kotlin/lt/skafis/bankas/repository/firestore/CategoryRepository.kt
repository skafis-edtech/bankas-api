package lt.skafis.bankas.repository.firestore

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Category
import org.springframework.stereotype.Repository
import java.text.Normalizer
import java.util.concurrent.ConcurrentHashMap

@Repository
class CategoryRepository(
    private val firestore: Firestore,
) : FirestoreCrudRepository<Category>(firestore, Category::class.java) {
    override val collectionPath = "categories"

    private val collectionCache = ConcurrentHashMap<String, Category>()

    fun getSearchPageableCategories(
        search: String,
        limit: Int,
        offset: Long,
    ): List<Category> {
        // Check if cache contains categories, use them if present
        val cachedCategories = collectionCache.values.toList()

        val documents =
            if (cachedCategories.isNotEmpty()) {
                // If cache is populated, use it
                cachedCategories
            } else {
                // If cache is empty, fetch from Firestore and update the cache
                val query =
                    firestore
                        .collection(collectionPath)
                        .orderBy("name")
                        .get()
                        .get()
                val fetchedDocuments = query.documents.mapNotNull { it.toObject(Category::class.java) }
                fetchedDocuments.forEach { collectionCache[it.id] = it }
                fetchedDocuments
            }

        // If search is not null, filter the documents based on the search criteria
        val filteredDocuments =
            if (search.isNotEmpty()) {
                val normalizedSearch = normalizeString(search)
                documents.filter { category ->
                    normalizeString(category.name).contains(normalizedSearch)
                }
            } else {
                documents
            }

        return filteredDocuments.drop(offset.toInt()).take(limit)
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

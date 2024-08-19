package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.model.Category
import org.springframework.stereotype.Repository
import java.text.Normalizer

@Repository
class CategoryRepository(
    private val firestore: Firestore,
) : FirestoreCrudRepository<Category>(firestore, Category::class.java) {
    override val collectionPath = "categories"

    fun getSearchPageableCategories(
        search: String,
        limit: Int,
        offset: Long,
    ): List<Category> {
        val collectionRef = firestore.collection(collectionPath)

        // Retrieve all documents if search is null, or retrieve a larger set to filter
        val query =
            if (search.isEmpty()) {
                collectionRef.orderBy("name")
            } else {
                collectionRef.orderBy("name")
            }.get().get()

        val documents = query.documents

        // If search is not null, filter the documents based on the search criteria
        val filteredDocuments =
            if (!search.isEmpty()) {
                val normalizedSearch = normalizeString(search)
                documents.filter { document ->
                    val category = document.toObject(Category::class.java)
                    category.name.let { normalizeString(it).contains(normalizedSearch) }
                }
            } else {
                documents
            }

        // Apply pagination
        val pagedDocuments = filteredDocuments.drop(offset.toInt()).take(limit.toInt())

        return pagedDocuments.mapNotNull { it.toObject(Category::class.java) }
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

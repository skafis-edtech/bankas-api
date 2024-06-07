package lt.skafis.bankas.category

import org.springframework.web.bind.annotation.*
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.DocumentReference
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.WriteResult
import org.springframework.beans.factory.annotation.Autowired

@RestController
@RequestMapping("/api/categories")
class CategoryController @Autowired constructor(
    private val firestore: Firestore
) {

    @GetMapping("/{id}")
    fun getCategory(@PathVariable id: String): Category? {
        val docRef: DocumentReference = firestore.collection("categories").document(id)
        val future: ApiFuture<com.google.cloud.firestore.DocumentSnapshot> = docRef.get()
        val document = future.get()
        return if (document.exists()) document.toObject(Category::class.java) else null
    }

    @PostMapping
    fun createCategory(@RequestBody category: Category): String {
        val future: ApiFuture<WriteResult> = firestore.collection("categories").document().set(category)
        return future.get().updateTime.toString()
    }

    @PutMapping("/{id}")
    fun updateCategory(@PathVariable id: String, @RequestBody category: Category): String {
        val future: ApiFuture<WriteResult> = firestore.collection("categories").document(id).set(category)
        return future.get().updateTime.toString()
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: String): String {
        val future: ApiFuture<WriteResult> = firestore.collection("categories").document(id).delete()
        return future.get().updateTime.toString()
    }
}

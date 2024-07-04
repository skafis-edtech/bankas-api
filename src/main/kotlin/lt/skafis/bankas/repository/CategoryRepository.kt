package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import lt.skafis.bankas.modelOld.Category
import org.springframework.stereotype.Repository

@Repository
class CategoryRepository(firestore: Firestore) : FirestoreCrudRepository<Category>(firestore, Category::class.java) {
    override val collectionPath = "categories"
}
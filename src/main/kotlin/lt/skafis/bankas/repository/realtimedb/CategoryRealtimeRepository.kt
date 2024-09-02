package lt.skafis.bankas.repository.realtimedb

import com.google.firebase.database.DatabaseReference
import lt.skafis.bankas.model.Category
import org.springframework.stereotype.Repository

@Repository
class CategoryRealtimeRepository(
    databaseReference: DatabaseReference,
) : RealtimeCrudRepository<Category>(
        databaseReference,
        Category::class.java,
    ) {
    override val collectionPath: String = "categories"
}

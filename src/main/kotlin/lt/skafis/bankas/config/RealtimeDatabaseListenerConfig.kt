package lt.skafis.bankas.config

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import lt.skafis.bankas.model.Category
import lt.skafis.bankas.repository.realtimedb.CategoryRealtimeRepository
import lt.skafis.bankas.service.RealtimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RealtimeDatabaseListenerConfig {
    @Autowired
    private lateinit var realtimeDatabase: DatabaseReference

    @Autowired
    private lateinit var categoryRealtimeRepository: CategoryRealtimeRepository

    @Autowired
    private lateinit var realtimeService: RealtimeService

    @Bean
    fun initializeCategoryListeners(): ValueEventListener {
        val categoryRef = realtimeDatabase.child("categories")
        realtimeService.updateCategories()

        val listener =
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categories = snapshot.children.mapNotNull { it.getValue(Category::class.java) }
                    categoryRealtimeRepository.setCollectionCache(categories.associateBy { it.id })
                    println("Category cache updated")
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to listen for category changes: ${error.message}")
                }
            }

        categoryRef.addValueEventListener(listener)
        return listener
    }
}

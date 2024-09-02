package lt.skafis.bankas.service

import lt.skafis.bankas.model.Category

/**
 * This service updates realtime db with changed data from firestore.
 * Reading from realtime db is not included. It is done from normal services.
 */
interface RealtimeService {
    fun getAllCategories(): List<Category>

    fun updateCategories()
}

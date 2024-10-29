package lt.skafis.bankas.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {
    @Value("\${category.unsorted.id}")
    lateinit var unsortedCategoryId: String
}

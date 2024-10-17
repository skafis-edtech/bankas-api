package lt.skafis.bankas.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.cloud.FirestoreClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class FirebaseConfig {
    private val serviceAccountPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_PATH")
    private val databaseUrl = System.getenv("FIREBASE_REALTIME_DB_URL") // Get database URL from environment variables

    @Bean
    fun firebaseApp(): FirebaseApp {
        val serviceAccount = FileInputStream(serviceAccountPath)
        val options =
            FirebaseOptions
                .builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(databaseUrl) // Use the database URL from the environment variable
                .build()
        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun firebaseAuth(firebaseApp: FirebaseApp): FirebaseAuth = FirebaseAuth.getInstance(firebaseApp)

    @Bean
    fun firestore(firebaseApp: FirebaseApp): Firestore = FirestoreClient.getFirestore(firebaseApp)

    @Bean
    fun storage(): Storage {
        val serviceAccount = FileInputStream(serviceAccountPath)
        return StorageOptions
            .newBuilder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
            .service
    }
}

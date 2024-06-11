package lt.skafis.bankas.repository

import com.google.cloud.storage.Blob
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
class AnswerStorageRepository(private val storage: Storage) {

    @Value("\${firebase.storage.bucket}")
    private lateinit var bucketName: String

    fun uploadImage(file: MultipartFile, fileName: String): String {
        val bucket: Bucket = storage.get(bucketName)
        val blob: Blob = bucket.create("answers/$fileName", file.bytes, file.contentType)
        return blob.mediaLink
    }

    fun getImageUrl(fileName: String): String {
        val bucket: Bucket = storage.get(bucketName)
        val blob: Blob = bucket.get("answers/$fileName")
        return blob.mediaLink
    }

    fun deleteImage(fileName: String) {
        val bucket: Bucket = storage.get(bucketName)
        val blob: Blob = bucket.get("answers/$fileName")
        blob.delete()
    }

    fun listImages(): List<String> {
        val bucket: Bucket = storage.get(bucketName)
        val blobs = bucket.list(Storage.BlobListOption.prefix("answers/"))
        return blobs.iterateAll().map { it.name }
    }
}
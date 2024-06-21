package lt.skafis.bankas.repository

import com.google.cloud.storage.Blob
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
class StorageRepository(
    private val storage: Storage,
) {

    @Value("\${firebase.storage.bucket}")
    private lateinit var bucketName: String

    fun uploadImage(file: MultipartFile, filePath: String): String {
        val bucket: Bucket = storage.get(bucketName)
        val blob: Blob = bucket.create(filePath, file.bytes, file.contentType)
        return blob.mediaLink
    }

    fun getImageUrl(filePath: String): String {
        val bucket: Bucket = storage.get(bucketName)
        val blob: Blob = bucket.get(filePath)
        return blob.mediaLink
    }

    fun deleteImage(filePath: String) {
        val bucket: Bucket = storage.get(bucketName)
        val blob: Blob = bucket.get(filePath)
        blob.delete()
    }

    fun listImages(dirPath: String): List<String> {
        val bucket: Bucket = storage.get(bucketName)
        val blobs = bucket.list(Storage.BlobListOption.prefix(dirPath))
        return blobs.iterateAll().map { it.name }
    }
}
package lt.skafis.bankas.controller

import lt.skafis.bankas.repository.ProblemStorageRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/problems")
class ProblemStorageController(private val repository: ProblemStorageRepository) {

    @PostMapping("/upload")
    fun uploadImage(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val fileName = file.originalFilename ?: return ResponseEntity.badRequest().body("Invalid file name")
        val mediaLink = repository.uploadImage(file, fileName)
        return ResponseEntity.ok(mediaLink)
    }

    @GetMapping("/download/{fileName}")
    fun getImageUrl(@PathVariable fileName: String): ResponseEntity<String> {
        return ResponseEntity.ok(repository.getImageUrl(fileName))
    }

    @DeleteMapping("/delete/{fileName}")
    fun deleteImage(@PathVariable fileName: String): ResponseEntity<Void> {
        repository.deleteImage(fileName)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/list")
    fun listImages(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(repository.listImages())
    }
}

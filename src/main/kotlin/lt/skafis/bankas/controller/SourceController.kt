package lt.skafis.bankas.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import lt.skafis.bankas.dto.SourcePostDto
import lt.skafis.bankas.model.Source
import lt.skafis.bankas.service.SourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/source")
@Tag(name = "Source Controller", description = "SUPER_ADMIN")
@SecurityRequirement(name = "bearerAuth")
class SourceController {

    @Autowired
    private lateinit var sourceService: SourceService

    @PostMapping
    fun createSource(@RequestBody sourcePostDto: SourcePostDto): ResponseEntity<Source> {
        val source = sourceService.createSource(sourcePostDto)
        return ResponseEntity.ok(source)
    }

    @GetMapping
    fun getAllSources(): ResponseEntity<List<Source>> {
        val sources = sourceService.getSources()
        return ResponseEntity.ok(sources)
    }

    @GetMapping("/{id}")
    fun getSourceById(@PathVariable id: String): ResponseEntity<Source> {
        val source = sourceService.getSourceById(id)
        return ResponseEntity.ok(source)
    }

    @PutMapping("/{id}")
    fun updateSource(@PathVariable id: String, @RequestBody sourcePostDto: SourcePostDto): ResponseEntity<Source> {
        val updatedSource = sourceService.updateSource(id, sourcePostDto)
        return ResponseEntity.ok(updatedSource)
    }

    @DeleteMapping("/{id}")
    fun deleteSource(@PathVariable id: String): ResponseEntity<Void> {
        sourceService.deleteSource(id)
        return ResponseEntity.ok().build()
    }
}
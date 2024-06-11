package lt.skafis.bankas.draft

import org.springframework.web.bind.annotation.*
import lt.skafis.bankas.model.ProblemMeta
import lt.skafis.bankas.repository.FirestoreMetaRepository

@RestController
@RequestMapping("/api/meta/problemMeta")
class MetaController(
    private val metaRepository: FirestoreMetaRepository
) {

    @GetMapping
    fun getProblemMeta(): ProblemMeta? {
        return metaRepository.getProblemMeta()
    }

    @PutMapping
    fun updateProblemMeta(@RequestBody problemMeta: ProblemMeta): String {
        val updated = metaRepository.updateProblemMeta(problemMeta)
        return if (updated) "Update successful" else "Update failed"
    }
}

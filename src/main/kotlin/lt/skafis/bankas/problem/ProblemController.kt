package lt.skafis.bankas.problem

import org.springframework.web.bind.annotation.*
import com.google.cloud.firestore.DocumentReference
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.WriteResult
import lt.skafis.bankas.model.Problem

@RestController
@RequestMapping("/api/problems")
class ProblemController (
    private val firestore: Firestore
) {

    @GetMapping("/{id}")
    fun getProblem(@PathVariable id: String): Problem? {
        val docRef: DocumentReference = firestore.collection("problems").document(id)
        val future: ApiFuture<com.google.cloud.firestore.DocumentSnapshot> = docRef.get()
        val document = future.get()
        return if (document.exists()) document.toObject(Problem::class.java) else null
    }

    @PostMapping
    fun createProblem(@RequestBody problem: Problem): String {
        val future: ApiFuture<WriteResult> = firestore.collection("problems").document(problem.id).set(problem)
        return future.get().updateTime.toString()
    }

    @PutMapping("/{id}")
    fun updateProblem(@PathVariable id: String, @RequestBody problem: Problem): String {
        val future: ApiFuture<WriteResult> = firestore.collection("problems").document(id).set(problem)
        return future.get().updateTime.toString()
    }

    @DeleteMapping("/{id}")
    fun deleteProblem(@PathVariable id: String): String {
        val future: ApiFuture<WriteResult> = firestore.collection("problems").document(id).delete()
        return future.get().updateTime.toString()
    }
}
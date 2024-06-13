package lt.skafis.bankas.repository

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QuerySnapshot
import lt.skafis.bankas.dto.ProblemViewDto
import org.springframework.stereotype.Repository

@Repository
class FirestoreProblemRepository(private val firestore: Firestore) {

    private val collectionPath = "problems"

    fun createProblem(problem: ProblemViewDto): String {
        val docRef = firestore.collection(collectionPath).document(problem.id)
        docRef.set(problem)
        return docRef.id
    }

    fun getProblemById(id: String): ProblemViewDto? {
        val docRef = firestore.collection(collectionPath).document(id)
        val docSnapshot = docRef.get().get()
        return if (docSnapshot.exists()) {
            val problemDto = docSnapshot.toObject(ProblemViewDto::class.java)
            problemDto?.let { ProblemViewDto(it.id, it.problemImage, it.answerImage, it.problemText, it.answerText, it.categoryId, it.createdOn) }
        } else {
            null
        }
    }

    fun updateProblem(problem: ProblemViewDto): Boolean {
        val docRef = firestore.collection(collectionPath).document(problem.id)
        return try {
            docRef.set(problem.copy(id = problem.id)).get()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteProblem(id: String): Boolean {
        val docRef = firestore.collection(collectionPath).document(id)
        return try {
            docRef.delete().get()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getAllProblems(): List<ProblemViewDto> {
        val collection = firestore.collection(collectionPath).get().get()
        return collection.documents.mapNotNull {
            val problemDto = it.toObject(ProblemViewDto::class.java)
            problemDto.let { dto -> ProblemViewDto(it.id, dto.problemImage, dto.answerImage, dto.problemText, dto.answerText, dto.categoryId, dto.createdOn) }
        }
    }

    fun countDocuments(): Long {
        val collectionRef = firestore.collection(collectionPath)
        val countQuery = collectionRef.count()
        val countQuerySnapshot = countQuery.get().get()
        return countQuerySnapshot.count
    }

    fun getProblemsByCategoryId(categoryId: String): List<ProblemViewDto> {
        val querySnapshot = firestore.collection(collectionPath)
            .whereEqualTo("categoryId", categoryId)
            .get()
            .get()

        return querySnapshot.documents.mapNotNull {
            val problemDto = it.toObject(ProblemViewDto::class.java)
            problemDto.let { dto -> ProblemViewDto(it.id, dto.problemImage, dto.answerImage, dto.problemText, dto.answerText, dto.categoryId, dto.createdOn) }
        }
    }
}

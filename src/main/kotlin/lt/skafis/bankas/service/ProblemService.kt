package lt.skafis.bankas.service

import lt.skafis.bankas.dto.ProblemPostDto
import lt.skafis.bankas.model.Problem

interface ProblemService {
    fun getProblems(): List<Problem>
    fun getProblemById(id: String): Problem
    fun createProblem(problemPostDto: ProblemPostDto): Problem
    fun updateProblem(id: String, problemPostDto: ProblemPostDto): Problem
    fun deleteProblem(id: String)
    fun utilsGetImageSrc(imagePath: String): String
}
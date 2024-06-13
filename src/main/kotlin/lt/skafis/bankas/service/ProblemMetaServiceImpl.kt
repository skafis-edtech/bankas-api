package lt.skafis.bankas.service

import lt.skafis.bankas.repository.FirestoreMetaRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class ProblemMetaServiceImpl(
    private val firestoreMetaRepository: FirestoreMetaRepository
) : ProblemMetaService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getLastUsedSkfCode(): String {
        log.info("Fetching last used SKF code")
        val problemMeta = firestoreMetaRepository.getProblemMeta() ?: throw NotFoundException("Problem meta with last used SKF code not found")
        val lastUsedCode = problemMeta.lastUsedCode
        if (lastUsedCode.isBlank()) {
            throw NotFoundException("Last used SKF code in problemMeta empty or not found")
        }
        log.info("Last used SKF code fetched successfully")
        return lastUsedCode
    }

    override fun incrementLastUsedSkfCode() {
        log.info("Incrementing last used SKF code")
        val problemMeta = firestoreMetaRepository.getProblemMeta() ?: throw NotFoundException("Problem meta with last used SKF code not found")
        val lastUsedCode = problemMeta.lastUsedCode
        if (lastUsedCode.isBlank()) {
            throw NotFoundException("Last used SKF code in problemMeta empty or not found")
        }
        val incrementedCode = incrementSkf(lastUsedCode)
        val success = firestoreMetaRepository.updateProblemMeta(problemMeta.copy(lastUsedCode = incrementedCode.toString()))
        if (!success) {
            throw NotFoundException("Failed to increment last used SKF code")
        }
        log.info("Last used SKF code incremented successfully")
    }

    private fun incrementSkf(skfCode: String): String {
        val regex = Regex("SKF-(\\d+)")
        val matchResult = regex.find(skfCode)

        return if (matchResult != null) {
            val number = matchResult.groupValues[1].toInt()
            val incrementedNumber = number + 1
            "SKF-$incrementedNumber"
        } else {
            throw IllegalArgumentException("Invalid SKF format")
        }
    }
}
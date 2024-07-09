package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.repository.MetaRepository
import lt.skafis.bankas.service.ProblemMetaService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class ProblemMetaServiceImp(
    private val firestoreMetaRepository: MetaRepository
) : ProblemMetaService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getIncrementedLastUsedSkfCode(): String {
        log.info("Fetching last used SKF code")
        val problemMeta = firestoreMetaRepository.getProblemMeta() ?: throw NotFoundException("Problem meta with last used SKF code not found")
        val lastUsedCode = problemMeta.lastUsedCode
        if (lastUsedCode.isBlank()) {
            throw NotFoundException("Last used SKF code in problemMeta empty or not found")
        }
        log.info("Last used SKF code fetched successfully")
        return incrementSkf(lastUsedCode)
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

    override fun initializeLastUsedSkfCode() {
        TODO("Not yet implemented")
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
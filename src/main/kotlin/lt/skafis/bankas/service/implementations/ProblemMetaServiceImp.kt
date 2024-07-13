package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.repository.MetaRepository
import lt.skafis.bankas.service.ProblemMetaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class ProblemMetaServiceImp: ProblemMetaService {

    @Autowired
    private lateinit var firestoreMetaRepository: MetaRepository

    override fun getIncrementedLastUsedSkfCode(): String {
        val problemMeta = firestoreMetaRepository.getProblemMeta() ?: throw NotFoundException("Problem meta with last used SKF code not found")
        val lastUsedCode = problemMeta.lastUsedCode
        if (lastUsedCode.isBlank()) {
            throw NotFoundException("Last used SKF code in problemMeta empty or not found")
        }
        return incrementSkf(lastUsedCode)
    }

    override fun incrementLastUsedSkfCode() {
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
    }

    override fun initializeLastUsedSkfCode() {
        firestoreMetaRepository.init()
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
package lt.skafis.bankas.service.implementations

import lt.skafis.bankas.repository.MetaRepository
import lt.skafis.bankas.service.ProblemMetaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Service
class ProblemMetaServiceImpl: ProblemMetaService {

    @Autowired
    private lateinit var firestoreMetaRepository: MetaRepository
    override fun getLowestUnusedSkfCode(): String {
        val problemMeta = firestoreMetaRepository.getProblemMeta() ?: throw NotFoundException("Problem meta not found")
        val usedSkfCodesNrs = problemMeta.usedSkfCodes.split(",")
            .filter { it.isNotEmpty() }
            .map { it.toInt() }
            .toSet()
        val lowestUnusedNumberUpToMillion = (1..1000000).first { !usedSkfCodesNrs.contains(it) }
        return "SKF-$lowestUnusedNumberUpToMillion"
    }

    override fun amendUsedSkfCodeList(skfCode: String) {
        val problemMeta = firestoreMetaRepository.getProblemMeta() ?: throw NotFoundException("Problem meta not found")
        val usedSkfCodesNrs = problemMeta.usedSkfCodes.split(",").toMutableList()
        val skfCodeNr = skfCode.split("-")[1].toInt()
        usedSkfCodesNrs.add(skfCodeNr.toString())
        val updatedProblemMeta = problemMeta.copy(usedSkfCodes = usedSkfCodesNrs.joinToString(","))
        firestoreMetaRepository.updateProblemMeta(updatedProblemMeta)
    }

    override fun removeSkfCodeFromUsedList(skfCode: String) {
        val problemMeta = firestoreMetaRepository.getProblemMeta() ?: throw NotFoundException("Problem meta not found")
        val usedSkfCodesNrs = problemMeta.usedSkfCodes.split(",").toMutableList()
        val skfCodeNr = skfCode.split("-")[1].toInt()
        usedSkfCodesNrs.remove(skfCodeNr.toString())
        val updatedProblemMeta = problemMeta.copy(usedSkfCodes = usedSkfCodesNrs.joinToString(","))
        firestoreMetaRepository.updateProblemMeta(updatedProblemMeta)
    }

    override fun clearUsedSkfCodeList() {
        val problemMeta = firestoreMetaRepository.getProblemMeta() ?: throw NotFoundException("Problem meta not found")
        val updatedProblemMeta = problemMeta.copy(usedSkfCodes = "")
        firestoreMetaRepository.updateProblemMeta(updatedProblemMeta)
    }


}
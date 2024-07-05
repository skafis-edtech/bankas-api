package lt.skafis.bankas.serviceOld

interface ProblemMetaService{
    fun getIncrementedLastUsedSkfCode(): String
    fun incrementLastUsedSkfCode()
}
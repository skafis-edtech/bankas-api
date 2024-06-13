package lt.skafis.bankas.service

interface ProblemMetaService{
    fun getIncrementedLastUsedSkfCode(): String
    fun incrementLastUsedSkfCode()
}
package lt.skafis.bankas.service

interface ProblemMetaService{
    fun getLowestUnusedSkfCode(): String
    fun amendUsedSkfCodeList(skfCode: String)
    fun removeSkfCodeFromUsedList(skfCode: String)
    fun clearUsedSkfCodeList()

}
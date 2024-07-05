package lt.skafis.bankas.modelOld

import java.time.Instant

data class Category (
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val author: String = "",
    val approvedBy: String = "",
    val approvedOn: String = Instant.now().toString(),
    val createdOn: String = "",
    val lastModifiedOn: String = "",
)
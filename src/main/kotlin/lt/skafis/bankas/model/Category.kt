package lt.skafis.bankas.model

data class Category(
    override var id: String = "",
    val name: String = "",
    val description: String = "",
): Identifiable 
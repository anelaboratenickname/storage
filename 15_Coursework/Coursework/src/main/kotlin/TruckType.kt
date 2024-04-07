enum class TruckType(
    val capacity: Int,
    val typeName: String
) {
    LOW_TONNAGE(2000, "малотоннажный"),
    MEDIUM_TONNAGE(5000, "среднетоннажный"),
    LARGE_TONNAGE(10000, "крупнотоннажный"),
    EXTRA_LARGE_TONNAGE(15000, "особо крупный");
}
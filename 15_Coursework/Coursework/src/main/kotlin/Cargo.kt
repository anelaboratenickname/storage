class Cargo {
    val content = mutableListOf<Product>()

    fun push(item: Product) {
        content.add(item)
    }

    fun pop(): Product? {
        return if (isEmpty()) {
            null
        } else {
            content.last()
            content.removeLast()
        }
    }

    fun isEmpty(): Boolean = content.isEmpty()
}

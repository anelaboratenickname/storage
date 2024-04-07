enum class LargeProducts : Product {
    FRIDGE {
        override val productName = "Холодильник"
        override val weight = 150
        override val time = 2000
    },

    COUCH {
        override val productName = "Диван"
        override val weight = 100
        override val time = 1600
    },

    BIKE {
        override val productName = "Мотоцикл"
        override val weight = 200
        override val time = 2000
    };

    companion object {
        const val TYPE_NAME = "Крупногабаритные"
        val minProductWeight = LargeProducts.entries.min().weight
    }
}
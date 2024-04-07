enum class MediumSizedProducts : Product {
    TV {
        override val productName = "Партия телевизоров"
        override val weight = 50
        override val time = 400
    },

    WASHING_MACHINE {
        override val productName = "Стиральная машина"
        override val weight = 60
        override val time = 800
    },

    TABLE {
        override val productName = "Партия столов"
        override val weight = 60
        override val time = 600
    };

    companion object {
        const val TYPE_NAME = "Среднегабаритные"
        val minProductWeight = MediumSizedProducts.entries.min().weight
    }
}
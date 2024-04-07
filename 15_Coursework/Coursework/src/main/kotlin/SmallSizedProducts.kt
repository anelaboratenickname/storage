enum class SmallSizedProducts : Product {
    MONITOR {
        override val productName = "Партия мониторов"
        override val weight = 30
        override val time = 200
    },

    MICROWAVE {
        override val productName = "Партия микроволновых печей"
        override val weight = 80
        override val time = 400
    },

    COFFEE_MACHINE {
        override val productName = "Партия кофемашин"
        override val weight = 60
        override val time = 280
    };

    companion object {
        const val TYPE_NAME = "Малогабаритные"
        val minProductWeight = SmallSizedProducts.entries.min().weight
    }
}
enum class FoodProducts : Product {
    POTATO {
        override val productName = "Мешок картофеля"
        override val weight = 25
        override val time = 200
    },

    MILK {
        override val productName = "Упаковка молока"
        override val weight = 15
        override val time = 160
    },

    WATER {
        override val productName = "Упаковка воды"
        override val weight = 15
        override val time = 160
    },

    BREAD {
        override val productName = "Ящик хлеба"
        override val weight = 10
        override val time = 80
    },

    EGGS {
        override val productName = "Упаковка яиц"
        override val weight = 15
        override val time = 160
    };

    companion object {
        const val TYPE_NAME = "Пищевые"
        val minProductWeight = FoodProducts.entries.min().weight
    }
}
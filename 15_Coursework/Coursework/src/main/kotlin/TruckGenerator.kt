import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

object TruckGenerator {
    private const val MIN_CARGO_WEIGHT = 1800
    private const val FOOD_CARGO_CHANCE = 25
    private var currentCapacity = 0
    private val generatorScope = CoroutineScope(Dispatchers.Default)
    private val minProductWeight = minOf(
        SmallSizedProducts.minProductWeight,
        MediumSizedProducts.minProductWeight,
        LargeProducts.minProductWeight
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun generateTruckToUnload() = generatorScope.produce(capacity = 6) {
        while (true) {
            val nextTruck = Truck(TruckType.entries.random())
            val randomCargoWeight = (MIN_CARGO_WEIGHT..nextTruck.capacity).random()
            currentCapacity = randomCargoWeight
            if (FOOD_CARGO_CHANCE.isRealize()) placeFoodProduct(nextTruck)
            else
                while (currentCapacity >= minProductWeight) {
                    var nextProduct: Product
                    val productType = (1..3).random()
                    when (productType) {
                        1 -> {
                            nextProduct = SmallSizedProducts.entries.random()
                            placeProduct(nextTruck, nextProduct)
                        }

                        2 -> {
                            nextProduct = MediumSizedProducts.entries.random()
                            placeProduct(nextTruck, nextProduct)
                        }

                        3 -> {
                            nextProduct = LargeProducts.entries.random()
                            placeProduct(nextTruck, nextProduct)
                        }
                    }
                }
            val filledCapacityPercentage =
                ((randomCargoWeight - currentCapacity).toDouble() / (nextTruck.capacity)) * 100
            println(
                "\n" + """${nextTruck.name} создан.
                |Загружен на ${filledCapacityPercentage.toInt()} %
                |Вес груза: ${randomCargoWeight - currentCapacity}
            """.trimMargin()
            )
            nextTruck.cargo.content.sortBy { it.productName }
            send(nextTruck)
            delay(30000)
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun generateTruckToLoad() = generatorScope.produce(capacity = 5) {
        delay(2000)
        while (true) {
            when ((1..2).random()) {
                1 -> send(Truck(TruckType.LOW_TONNAGE))
                2 -> send(Truck(TruckType.MEDIUM_TONNAGE))
            }
        }
    }

    private fun placeProduct(truck: Truck, product: Product) {
        if (product.weight <= currentCapacity) {
            truck.cargo.push(product)
            currentCapacity -= product.weight
        }
    }

    private fun placeFoodProduct(truck: Truck) {
        while (currentCapacity >= FoodProducts.minProductWeight) {
            val product = FoodProducts.entries.random()
            if (product.weight <= currentCapacity) {
                truck.cargo.push(product)
                currentCapacity -= product.weight
            }
        }
    }

    private fun Int.isRealize(): Boolean {
        return this >= (1..100).random()
    }
}
import TruckGenerator.generateTruckToLoad
import TruckGenerator.generateTruckToUnload
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Storage {
    private val unsorted = mutableListOf<Product>()
    private val sortedFood = mutableListOf<Product>()
    private val sortedSmallSizedProducts = mutableListOf<Product>()
    private val sortedMediumSizedProducts = mutableListOf<Product>()
    private val sortedLargeProducts = mutableListOf<Product>()
    private val unloadingScope = CoroutineScope(Dispatchers.Default)
    private val loadingScope = CoroutineScope(Dispatchers.Default)
    private val statusBarScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()
    private var sentTrucksCount = 0

    suspend fun unload() = withContext(unloadingScope.coroutineContext) {
        val unloading = generateTruckToUnload()

        repeat(UNLOADING_PORTS_COUNT) { port ->
            launch {
                unloading.consumeEach { truck ->
                    println(GREEN + "${port + 1} ПОРТ РАЗГРУЗКИ - ${truck.name} поступил на разгрузку" + RESET)
                    printStatusBar()
                    delay(1000)
                    moveElementsToStorage(truck)
                }
            }
        }
    }

    private suspend fun moveElementsToStorage(truck: Truck) {
        println(GREEN + "Идет разгрузка машины - ${truck.name}" + RESET)
        printStatusBar()
        truck.productFlow().collect { product ->
            if (product != null) {
                delay(product.time.toLong())
                unsorted.add(product)
            }
        }
        println("\n" + GREEN + "${truck.name} разгружен" + RESET)
        printStatusBar()
        sort()
    }

    private val unsortedFlow = flow {
        while (unsorted.isNotEmpty()) {
            val next = unsorted.random()
            unsorted.remove(next)
            emit(next)
        }
    }

    private suspend fun sort() = unloadingScope.launch {
        println("Идет сортировка...")
        printStatusBar()
        mutex.withLock {
            unsortedFlow.collect { product ->
                when (product) {
                    is SmallSizedProducts -> {
                        sortedSmallSizedProducts.add(product)
                        sortedSmallSizedProducts.sortBy { it.productName }
                    }

                    is MediumSizedProducts -> {
                        sortedMediumSizedProducts.add(product)
                        sortedMediumSizedProducts.sortBy { it.productName }
                    }

                    is LargeProducts -> {
                        sortedLargeProducts.add(product)
                        sortedLargeProducts.sortBy { it.productName }
                    }

                    is FoodProducts -> {
                        sortedFood.add(product)
                        sortedFood.sortBy { it.productName }
                    }
                }
            }
        }
    }

    suspend fun load() = withContext(loadingScope.coroutineContext) {
        val loading = generateTruckToLoad()

        repeat(LOADING_PORTS_COUNT) { port ->
            launch {
                loading.consumeEach { truck ->
                    println(BLUE + "${port + 1} ПОРТ ЗАГРУЗКИ - ${truck.name} поступил на загрузку" + RESET)
                    printStatusBar()
                    delay(2000)
                    getProductType(truck)
                }
            }
        }
    }

    private suspend fun getProductType(truck: Truck) {
        when ((1..4).random()) {
            1 -> getProductsFromStorage(
                truck,
                sortedFood,
                FoodProducts.minProductWeight,
                FoodProducts.TYPE_NAME
            )

            2 -> getProductsFromStorage(
                truck,
                sortedSmallSizedProducts,
                SmallSizedProducts.minProductWeight,
                SmallSizedProducts.TYPE_NAME
            )

            3 -> getProductsFromStorage(
                truck,
                sortedMediumSizedProducts,
                MediumSizedProducts.minProductWeight,
                MediumSizedProducts.TYPE_NAME
            )

            4 -> getProductsFromStorage(
                truck,
                sortedLargeProducts,
                LargeProducts.minProductWeight,
                LargeProducts.TYPE_NAME
            )
        }
    }

    private suspend fun getProductsFromStorage(
        truck: Truck,
        productList: MutableList<Product>,
        minProductWeight: Int,
        productType: String
    ) {
        var currentCargoWeight = 0
        val addedProducts = mutableListOf<Product>()
        println(BLUE + "Идет загрузка машины - ${truck.name}" + RESET)
        printStatusBar()
        while (truck.capacity - currentCargoWeight >= minProductWeight) {
            mutex.withLock {
                productList.forEach { product ->
                    if (product.weight <= (truck.capacity - currentCargoWeight)) {
                        currentCargoWeight += product.weight
                        delay(product.time.toLong())
                        truck.cargo.push(product)
                        addedProducts.add(product)
                    }
                }
                productList.removeAll(addedProducts)
                addedProducts.clear()
            }
        }
        sentTrucksCount++
        println(
            "\n" + BLUE + """${truck.name} загружен
            |Вес груза: $currentCargoWeight
            |Тип товаров: $productType
            |Количество отправленных грузовиков: $sentTrucksCount
        """.trimMargin() + RESET
        )
        observer()
        printStatusBar()
    }

    private suspend fun printStatusBar() {
        statusBarScope.coroutineContext.job.cancelChildren()
        statusBarScope.launch {
            delay(4000)
            StatusBar.print.cancellable().collect { print(it) }
        }
    }

    private fun observer() {
        if (sentTrucksCount == REQUIRED_NUMBER) {
            unloadingScope.cancel()
            loadingScope.cancel()
            statusBarScope.cancel()
        }
    }

    companion object {
        const val UNLOADING_PORTS_COUNT = 3
        const val LOADING_PORTS_COUNT = 5
        const val REQUIRED_NUMBER = 10
        const val GREEN = "\u001b[32m"
        const val BLUE = "\u001b[34m"
        const val RESET = "\u001b[0m"
    }
}
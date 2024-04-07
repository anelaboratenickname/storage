import kotlinx.coroutines.flow.flow

class Truck(
    type: TruckType
) {
    val name = "Грузовик <${(1..100).random()}> (${type.typeName})"
    var capacity = type.capacity
    val cargo = Cargo()

    fun productFlow() = flow {
        while (!cargo.isEmpty()) {
            emit(cargo.pop())
        }
    }
}
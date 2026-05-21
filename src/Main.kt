import java.time.Year
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val heavyTransport = VehicleFleet<Vehicle>("Transport ForHeavyGoods")
    val lightTransport = VehicleFleet<Vehicle>("Passenger Transport")
    val taxiCar = Car("GHY-2234","BMW",1990,100,workingStatus = true)
    val taxiCar2 = Car("GOO-2234","BMW",1980,90,workingStatus = true)
    val cargoCar = Truck("JJJ-8299", "Audi", 1979,2000,workingStatus = true)
    val cargoCar2 = Truck("KKK-8499", "Toyota", 1980,2500,workingStatus = false)
    val cargoCar3 = Truck("TTT-8299", "Audi", 1979,800,workingStatus = true)
    val schoolBus = Bus("PPP-2534","Lada",1970,1000,workingStatus = true)
    heavyTransport.addVehicle(cargoCar)
    heavyTransport.addVehicle(cargoCar2)
    heavyTransport.addVehicle(cargoCar3)
    lightTransport.addVehicle(taxiCar)
    lightTransport.addVehicle(taxiCar2)
    val allTransports = listOf(heavyTransport,lightTransport)
    val theMostRecent = allTransports.findNewestVehicle()
    val biggestAmountOfPassengers = allTransports.findMaxPassengerVehicle()
    val passengerOnBoard = taxiCar.board(1)
    val passengerExit = schoolBus.unboard(9)
    val load = heavyTransport.getAllTrucksMaxLoad()
    println(load)
    println(passengerOnBoard)
    println(passengerExit)
    val carWorking = heavyTransport.getVehicles()
        .filterIsInstance<Truck>()
        .find { it.workingStatus }
    if (carWorking != null) {
        try {
            println("Working car was found ${carWorking.transportModel} ${carWorking.load(4.0)} ")
        } catch (e: Exception) {
            println("Can't load cargo")
        }
    } else {
        println("There are no working heavy vehicles")
    }
    println(carWorking)
    val curLoad = carWorking?.load(24.00)
    println(curLoad)
}
//базовый класс транспорт
open class Vehicle(
    val vin: String,
    val transportModel: String,
    val productionYear: Int,
    var carMileage: Int,
    var workingStatus: Boolean
) {
}
//легковые машины
class Car(
    vin: String,
    transportModel: String,
    productionYear: Int,
    carMileage: Int,
    workingStatus: Boolean,
) : Vehicle(vin,transportModel,productionYear,carMileage,workingStatus), PassengerTransport {
    override var currentPassengers: Int = 2
    override val maxPassengers: Int = 4
}
//пассажирский транспорт
class Bus(
    vin: String,
    transportModel: String,
    productionYear: Int,
    carMileage: Int,
    workingStatus: Boolean
) : Vehicle(vin,transportModel,productionYear,carMileage,workingStatus), PassengerTransport {
    override var currentPassengers: Int = 10
    override val maxPassengers: Int = 30
}
//грузовой транспорт
class Truck(
    vin: String,
    transportModel: String,
    productionYear: Int,
    carMileage: Int,
    workingStatus: Boolean,
) : Vehicle(vin,transportModel,productionYear,carMileage,workingStatus), CargoTransport {
    override val maxLoadCapacity: Double = 40.0
    override var currentLoad: Double = 1.0
}
interface PassengerTransport {
    val maxPassengers: Int
    var currentPassengers: Int
//проверяем не превышает ли общее количество пассажиров наш максимум
//если превышает то выбрасываем ошибку иначе игнорируем if и возвращаем обновленное количество пассажиров
    fun board(count: Int): Int {
        if (currentPassengers + count > maxPassengers) {
            throw VehicleException("Too many passengers")
        }
        currentPassengers += count
        return currentPassengers
    }
    //функция высадки. если количество высаживаемых больше количества текущих пассажиров то выбрасываем ошибку
    //иначе вычитаем из текущего количества пассажиров, количество высаживаемых пассажиров
    fun unboard(count: Int): Int {
        if (count > currentPassengers) {
            throw IllegalArgumentException("There are more passengers then can exit a vehicle")
        }
        currentPassengers -= count
        return currentPassengers
    }
}
interface CargoTransport {
    val maxLoadCapacity: Double
    var currentLoad: Double
    fun load(weight: Double): Double {
        if (currentLoad + weight > maxLoadCapacity) {
            throw IllegalArgumentException("Too much weight")
        }
        currentLoad = weight + currentLoad
        return currentLoad
    }
    //создаем переменную убранный вес что бы ее вернуть и обнуляем текущий вес
    fun unload(): Double {
        val removedWeight = currentLoad
        return removedWeight
        currentLoad = 0.0
    }
}
// указываем лист что бы работать с несколькими автопарками
fun List<VehicleFleet<out Vehicle>>.findNewestVehicle(): Vehicle? {
    return this
        .flatMap { it.getVehicles() }
        .maxByOrNull {it.productionYear}
}
fun List<VehicleFleet<out Vehicle>>.findMaxPassengerVehicle(): PassengerTransport? {
    return this
        .flatMap { it.getAllPassengerTransport() }
        .maxByOrNull { it.maxPassengers }
}
class VehicleException(message: String) : RuntimeException(message)
//объявляем класс имя VehicleFleet, T - любой тип, используем только vehicle и его наследников
//Т:Vehicle - ограничение на использование только Vehicle
class VehicleFleet<T :Vehicle>(val fleetName: String) {
    // создаем конст. техника - коллекция, изменяемая, уникальные, инициализируем пустым
    // нет дубликатов
    private val vehicles: MutableSet<T> = mutableSetOf()
    fun addVehicle(vehicle: T) {
        //add возвращает тру или фолс
        if (!vehicles.add(vehicle)) {
            throw VehicleException("Transport already added to fleet")
        }
    }
    fun removeVehicle(vehicle: T) {
        vehicles.remove(vehicle)
    }
    //mutable list
    fun getVehicles(): List<T> {
        return vehicles.toList()
    }
    fun getAllTrucksMaxLoad(): Double {
        return vehicles
            .filterIsInstance<CargoTransport>()
            .sumOf { it.maxLoadCapacity }
    }
    fun getAllPassengerTransport(): List<PassengerTransport> {
        return vehicles.filterIsInstance<PassengerTransport>()
    }
}





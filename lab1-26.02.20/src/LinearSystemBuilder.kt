import java.io.BufferedReader
import java.lang.Exception
import kotlin.math.abs

/**
 * Создаёт структуру данных для СЛАУ.
 * @property allowedSize разрешённый диапазон размера СЛАУ.
 * @property allowedRandomRange разрешённый диапозон случайных значений коэффицетов СЛАУ.
 * @author Артемий Кульбако.
 */
class LinearSystemBuilder {

    var allowedSize = (1..20)
    var allowedRandomRange = (1..1000)

    /**
     * Читает построчно переданный BufferedReader и парсит его на наличие данных, необходимых для создания матрицы.
     * @see BufferedReader
     * @param reader служит для чтения данных.
     * @return созданную из потока данных СЛАУ.
     * @throws NumberFormatException если не удаётся распарсить данные; если размер матрицы не принаджлежит промежутку allowedSize.
     */
    fun read(reader: BufferedReader): LinearSystem {
        val rawNumbers = mutableListOf<MutableList<Double>>()
        val size = reader.readLine().trim().toInt()
        if (size !in allowedSize) throw NumberFormatException()
        for (i in 1..size) {
            val equation = reader.readLine().trim().split(" ")
            if (equation.size == size + 1) rawNumbers.add(equation.map { number -> number.toDouble() }.toMutableList())
            else throw Exception("Для уравнения №$i не задано решение или пропущен один из аргументов.")
        }
        val v = rawNumbers.map { it.last() }.toDoubleArray()
        rawNumbers.map { it.removeLast() }
        return LinearSystem(rawNumbers.map { it.toDoubleArray() }.toTypedArray(), v)
    }

    /**
     * Создаёт матрицу, заполненную случайными коэффицентами.
     * @param size размер случайной матрицы.
     * @return СЛАУ с случайно-сгенерированными коэффицентами.
     * @throws NumberFormatException если размер матрицы не принаджлежит промежутку allowedSize.
     */
    fun generateRandom(size: Int, isDiagPrev: Boolean): LinearSystem {
        if (size !in allowedSize) throw NumberFormatException()
        val numbers = Array(size) { DoubleArray(size) {allowedRandomRange.random().toDouble()} }
        if (isDiagPrev) numbers.forEachIndexed { i, arr -> arr[i] = arr.sumByDouble { abs(it) } * allowedRandomRange.random() }
        return LinearSystem(numbers, DoubleArray(size) { allowedRandomRange.random().toDouble() }
        )
    }
}
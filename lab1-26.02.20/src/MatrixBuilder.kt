import java.io.BufferedReader
import java.lang.Exception
import java.lang.IllegalArgumentException

/**
 * Структура данных для системы линейных алгебраических уравнений.
 * @property equations коэффиценты при уравнениях в системе.
 * @property resVector результирующий вектор системы.
 * @property size размерность матрицы.
 * @throws IllegalArgumentException если количество уравнений системы не совпадает с количеством ответов к ней.
 * @author Артемий Кульбако.
 */
data class LinearSystem constructor (var equations: Array<DoubleArray>, var resVector: DoubleArray,
                        var size: Int = if (equations.size == resVector.size) resVector.size
                        else throw IllegalArgumentException("Количество уравнений системы не совпадает с количеством ответов к ней."))

/**
 * Создаёт структуру данных для СЛАУ.
 * @property allowedSize разрешённый диапазон размера СЛАУ.
 * @property allowedRandomRange разрешённый диапозон случайных значений коэффицетов СЛАУ.
 * @author Артемий Кульбако.
 */
class LinearSystemBuilder {

    var allowedSize = (1..20)
    var allowedRandomRange = (1..100)

    /**
     * Читает построчно переданный BufferedReader и парсит его на наличие данных, необходимых для создания матрицы.
     * @see BufferedReader
     * @param reader служит для чтения данных.
     * @return созаднную из потока данных СЛАУ.
     * @throws NumberFormatException если не удаётся распарсить данные; если размер матрицы не принаджлежит промежутку allowedSize.
     */
    fun read(reader: BufferedReader): LinearSystem {
        val numbers = mutableListOf<MutableList<Double>>()
        val size = reader.readLine().trim().toInt()
        if (size !in allowedSize) throw NumberFormatException()
        for (i in 1..size) {
            var equation = reader.readLine().trim().split(" ")
            if (equation.size == size + 1) numbers.add(equation.map { number -> number.toDouble() }.toMutableList())
            else throw Exception("Для уравнения №$i не задано решение или пропущен один из аргументов.")
        }
        val resV = mutableListOf<Double>()
        numbers.forEach {
            val equationRes = it.last()
            resV.add(equationRes)
            it.remove(equationRes)
        }
        return LinearSystem(numbers.map { it.toDoubleArray() }.toTypedArray(), resV.toDoubleArray())
    }

    /**
     * Создаёт матрицу, заполненную случайными коэффицентами.
     * @param size размер случайной матрицы.
     * @return СЛАУ с случайно-сгенерированными коэффицентами.
     * @throws NumberFormatException если размер матрицы не принаджлежит промежутку allowedSize.
     */
    fun generateRandom(size: Int): LinearSystem {
        if (size !in allowedSize) throw NumberFormatException()
        val numbers = Array(size, { DoubleArray(size, {allowedRandomRange.random().toDouble()}) })
        val resV = DoubleArray(size, { allowedRandomRange.random().toDouble() })
        return LinearSystem(numbers, resV)
    }
}
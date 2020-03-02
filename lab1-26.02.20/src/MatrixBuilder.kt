import java.io.BufferedReader
import java.lang.IllegalArgumentException

/**
 * Структура данных для системы линейных алгебраических уравнений.
 * @property equations коэффиценты при уравнениях в системе.
 * @property resVector результирующий вектор системы.
 * @property size размерность матрицы.
 * @throws IllegalArgumentException если количество уравнений системы не совпадает с количеством ответов к ней.
 * @author Артемий Кульбако.
 */
data class LinearSystem(var equations: MutableList<MutableList<Double>>, var resVector: MutableList<Double>,
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
        reader.use {
            val size = it.readLine().trim().toInt()
            if (size !in allowedSize) throw NumberFormatException()
            for (i in 1..size)
numbers.add(it.readLine().trim().split(" ").map { number -> number.toDouble() }.toMutableList())
        }
        val resV = mutableListOf<Double>()
        numbers.forEach {
            val equationRes = it.last()
            resV.add(equationRes)
            it.remove(equationRes)
        }
        return LinearSystem(numbers, resV)
    }

    /**
     * Создаёт матрицу, заполненную случайными коэффицентами.
     * @param size размер случайной матрицы.
     * @return СЛАУ с случайно-сгенерированными коэффицентами.
     * @throws NumberFormatException если размер матрицы не принаджлежит промежутку allowedSize.
     */
    fun generateRandom(size: Int): LinearSystem {
        if (size !in allowedSize) throw NumberFormatException()
        val numbers = mutableListOf<MutableList<Double>>()
        val resV = mutableListOf<Double>()
        for (i in 1..size) {
            var newEquation = mutableListOf<Double>()
            for (j in 1..size) newEquation.add(allowedRandomRange.random().toDouble())
            numbers.add(newEquation)
            resV.add(allowedRandomRange.random().toDouble())
        }
        return LinearSystem(numbers, resV)
    }
}
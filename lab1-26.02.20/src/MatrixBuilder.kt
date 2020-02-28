import java.io.BufferedReader

/**
 * Структура данных для системы линейных алгебраических уравнений.
 * @property infelicity задаёт погрешность решения, по умолчанию 0.000001.
 * @property size устанавливает размер матрицы.
 * @property matrix коэффиценты уравнений системы.
 * @author Артемий Кульбако
 */
data class Matrix(var infelicity: Double = 0.000001, var size: Int, var matrix: MutableList<List<Double>>)
//TODO: в конструкторе проверяет правильность параметров, а не метод read

/**
 * Создаёт структуру данных для СЛАУ.
 */
class MatrixBuilder {

    /**
     * Читает построчно переданный BufferedReader и парсит его на наличие данных, необходимых для создания матрицы.
     * @see BufferedReader
     * @param reader служит для чтения данных.
     * @return Matrix
     * @throws NumberFormatException если не удаётся распарсить данные.
     */
    fun read(reader: BufferedReader): Matrix {
        reader.use {
            val infelicity = it.readLine().trim().toDouble()
            //if (infelicity > 1) throw NumberFormatException()
            val size = it.readLine().trim().toInt()
            //if (size !in (1..25)) throw NumberFormatException()
            val numbers = mutableListOf<List<Double>>()
            for (i in 1..size) numbers.add(it.readLine().trim().split(" ").map { number -> number.toDouble() })
            return Matrix(infelicity, size, numbers)
        }
    }

    /**
     * @return
     */
    fun generateRandom(size: Int) : Matrix {
        return Matrix(0.000001, size, mutableListOf<List<Double>>())
    }
}
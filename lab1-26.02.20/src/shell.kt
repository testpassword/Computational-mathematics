import java.io.*
import java.lang.IllegalArgumentException

/**
 * @author
 */
fun main(args: Array<String>) {
    try {
        val supporter = MatrixBuilder()
        val matrix: Matrix = when (args[0]) {
            "-f" -> {
                println("Чтение из файла ${args[1]}...")
                supporter.read(File(args[1]).bufferedReader())
            }
            "-k" -> {
                println("Можете вводить данные.")
                supporter.read(BufferedReader(InputStreamReader(System.`in`)))
            }
            "-r" -> {
                println("Генерирование матрицы размерностью ${args[1]}...")
                supporter.generateRandom(args[1].toInt())
            }
            "-h" -> throw Exception()
            else -> throw IllegalArgumentException()
        }
        println("Структура данных матрицы создана.")
        MatrixSolver.solveByGaussSeidel(matrix)
    } catch (e: ArrayIndexOutOfBoundsException) {
        System.err.println("Ключ запуска программы отсутствует. Используйте ключ -h для просмотра справки.")
    } catch (e: NumberFormatException) {
        System.err.println("Точность [0.000001 ; 1], размер матрицы [1 ; 20].")
    } catch (e: IllegalArgumentException) {
        System.err.println("Ключ задан неправильно.")
    } catch (e: FileNotFoundException) {
        System.err.println(e.localizedMessage)
    } catch (e: Exception) {
        println(("""*** Режимы работы программы: ****
                    ~   [-f path] для чтения матрицы из файла,
                    ~   [-k] для ввода матрицы с клавиатуры,
                    ~   [-r size] для генерирования случайной матрицы размерности size * size.
                    ~  *** Формат матрицы: ***
                    ~   погрешность размерность
                    ~   a[i] a[i+1] a[n] b[i]
                    ~  *** Пример: ***
                    ~   0.000001 3
                    ~   10 2 8 5
                    ~   2 3 1 24.42
                    ~   1 13 14 46""".trimMargin("~")))
    }
}
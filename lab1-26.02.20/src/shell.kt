import java.io.*
import java.lang.IllegalArgumentException

/**
 * Осуществляет взаимодействие с пользователем.
 * @param args аргументы командной строки.
 * @author Артемий Кульбако.
 */
fun main(args: Array<String>) {
    try {
        val supporter = LinearSystemBuilder()
        val keyboardReader = BufferedReader(InputStreamReader(System.`in`))
        val linSys = when (args[0]) {
            "-f" -> {
                println("Чтение из файла ${args[1]}...")
                supporter.read(File(args[1]).bufferedReader())
            }
            "-k" -> {
                println("Введите размер матрицы, а саму расширенную матрицу системы:")
                supporter.read(keyboardReader)
            }
            "-r" -> {
                println("Генерирование матрицы размерностью ${args[1]}...")
                supporter.generateRandom(args[1].toInt())
            }
            "-h" -> {
                println(("""*** Режимы работы программы: ****
                        ~   [-f path] для чтения матрицы из файла,
                        ~   [-k] для ввода матрицы с клавиатуры,
                        ~   [-r size] для генерирования случайной матрицы размерности size * size.
                        ~*** Формат матрицы: ***
                        ~   размерность
                        ~   a[i] a[i+1] a[n] b[i]
                        ~*** Пример: ***
                        ~   3
                        ~   10 2 8 5
                        ~   2 3 1 24.42
                        ~   1 13 14 46""".trimMargin("~")))
                kotlin.system.exitProcess(0)
            }
            else -> throw IllegalArgumentException("Ключ задан неправильно.")
        }
        println("Структура данных матрицы создана.\n${linSys}Введите погрешность ε [0.000001 ; 1]")
        val infelicity = keyboardReader.use { it.readLine().trim().toDouble() }
        if (infelicity > 1) throw NumberFormatException()
        val unknownVector = MatrixSolver.solveByGaussSeidel(linSys, infelicity, false)
    } catch (e: ArrayIndexOutOfBoundsException) {
        System.err.println("Ключ запуска программы отсутствует. Используйте ключ -h для просмотра справки.")
    }  catch (e: NumberFormatException) {
        System.err.println("Точность [0.000001 ; 1], размер матрицы [1 ; 20].")
    } catch (e: Exception) {
        System.err.println(e.localizedMessage)
    }
}
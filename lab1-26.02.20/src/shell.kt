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
        val keyReader = BufferedReader(InputStreamReader(System.`in`))
        val linSys = when (args[0]) {
            "-f" -> {
                println("Чтение из файла ${args[1]}...")
                supporter.read(File(args[1]).bufferedReader())
            }
            "-k" -> {
                println("Введите размер матрицы, и саму расширенную матрицу системы:")
                supporter.read(keyReader)
            }
            "-r" -> {
                println("Генерирование матрицы размерностью ${args[1]}...")
                supporter.generateRandom(args[1].toInt(), true)
            }
            "-h" -> {
                println(("""*** Режимы работы программы: ****
                        ~ [-f path] для чтения матрицы из файла,
                        ~ [-k] для ввода матрицы с клавиатуры,
                        ~ [-r size] для генерирования случайной матрицы размерности size * size [1 ; 20].
                        ~ Максимальный размер элемента матрицы = 1000.
                        ~ Целая часть должна отделяться от дробной точкой 
                        ~*** Формат матрицы: ***
                        ~ размерность
                        ~ a[i] a[i+1] a[n] b[i]
                        ~*** Пример: ***
                        ~ 3
                        ~ 10 2 8 5
                        ~ 2 3 1 24.42
                        ~ 1 13 14 46""".trimMargin("~")))
                kotlin.system.exitProcess(0)
            }
            else -> throw IllegalArgumentException("Ключ задан неправильно.")
        }
        println("""Структура данных для СЛАУ создана.
                ~$linSys
                ~Введите точность [0.000001 ; 1]""".trimMargin("~"))
        val precision = keyReader.readLine().trim().toDouble()
            .let { if ((it > 1) || (it < 0.000001)) throw NumberFormatException() else it}
        println("Введите x0. Если хотить оставить их = 0, то вводить ничего не нужно")
        val startApproxes = keyReader.use { it.readLine() }.trim()
        val answer = if (startApproxes == "") LinearSystemSolver.solveByGaussSeidel(linSys, precision, true)
        else LinearSystemSolver.solveByGaussSeidel(linSys, precision, true, startApproxes.split(" ").map { it.toDouble() }.toDoubleArray())
        println("""Вектор неизвестных = ${answer.xVector.contentToString()}
                ~Погрешности = ${answer.infelicity.contentToString()}
                ~Количество итераций = ${answer.counter}""".trimMargin("~"))
    } catch (e: ArrayIndexOutOfBoundsException) {
        System.err.println("Ключ запуска программы отсутствует. Используйте ключ -h для просмотра справки.")
    }  catch (e: NumberFormatException) {
        System.err.println("Точность [0.000001 ; 1], размер матрицы [1 ; 20].")
    } catch (e: Exception) {
        System.err.println(e.localizedMessage)
    }
}
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.NumberFormatException

/**
 * Осуществляет взаимодействие с пользователем.
 * @param args аргументы командной строки.
 * @author Артемий Кульбако.
 * @version 0.2
 */
fun main(args: Array<String>) {
    val keyReader = BufferedReader(InputStreamReader(System.`in`))
    keyReader.use {
        println("Введите номер желаемой функции:")
        var inputIsEnded = false
        while (!inputIsEnded) {
            try {
                val funcNumber = it.readLine().trim().toInt()
                if (funcNumber in 1..5) {
                    //TODO: Создать объект, представляющий функцию для интегрирования
                    println("Введите пределы интегрирования через пробел:")
                    while (!inputIsEnded) {
                        try {
                            val limits = it.readLine().trim().split(" ").map { n -> n.toDouble() }
                            if (limits.size == 2) {
                                println("Введите точность:")
                                while (!inputIsEnded) {
                                    try {
                                        val precision = it.readLine().trim().toDouble()
                                        if (precision > 1 || precision < 0.000001) throw NumberFormatException()
                                        inputIsEnded = true
                                        //TODO: Вызвать численный метод
                                    } catch (e: NumberFormatException) {
                                        printError("Ошибка ввода: введите число в [0.000001 ; 1].")
                                    }
                                }
                            } else throw NumberFormatException()
                        } catch (e: NumberFormatException) {
                            printError("Ошибка ввода: введите два числа через пробел.")
                        }
                    }
                } else throw NumberFormatException()
            } catch (e: NumberFormatException) {
                printError("Ошибка ввода: введите целое число в [1 ; 5].")
            }
        }
    }
}

var counter = 0
fun printError(msg: String) {
    counter++
    if (counter == 100) println("Ты чо дурак!? Дифиченто, блин!")
    System.err.println(msg)
}
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.NumberFormatException
import kotlin.math.pow

/**
 * Осуществляет взаимодействие с пользователем.
 * @param args аргументы командной строки.
 * @author Артемий Кульбако.
 * @version 0.3
 */
fun main(args: Array<String>) {
    val keyReader = BufferedReader(InputStreamReader(System.`in`))
    val functions = buildFunctions()
    keyReader.use {
        var continueInput = true
        println("Введите номер желаемой функции:")
        functions.forEachIndexed { i, el -> println("${i} - ${el.description}") }
        while (continueInput) {
            try {
                val funcNumber = it.readLine().trim().toInt()
                if (funcNumber in functions.indices) {
                    println("Введите пределы интегрирования через пробел:")
                    while (continueInput) {
                        try {
                            val limits = it.readLine().trim().split(" ").map { n -> n.toDouble() }
                            if (limits.size == 2) {
                                println("Введите точность:")
                                while (continueInput) {
                                    try {
                                        val precision = it.readLine().trim().toDouble().let {
                                            n -> if (n > 1 || n < 0.000001) throw NumberFormatException() else n
                                        }
                                        continueInput = false
                                        val answer = IntegralSolver.integrateByTrapezoid(functions[funcNumber], Pair(limits[0], limits[1]), precision)
                                        println("""Значение интеграла = ${answer.resValue}
                                                ~Количество разбиений = ${answer.blocks}
                                                ~Погрешность = ${answer.infelicity}""".trimMargin("~"))
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

//TODO: +4 функции
fun buildFunctions(): MutableList<MathFunction> {
    return mutableListOf(
        object: MathFunction {
        override fun func(xParameter: Double): Double {
            return xParameter.pow(2)
        }
        override val description: String
            get() = "x^2"
        }
    )
}

var counter = 0
fun printError(msg: String) {
    counter++
    if (counter == 100) println("Ты чо дурак!? Дифиченто, блин!")
    System.err.println(msg)
}
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.NumberFormatException
import kotlin.math.*

/**
 * Осуществляет взаимодействие с пользователем.
 * @param args аргументы командной строки.
 * @author Артемий Кульбако.
 * @version 1.0
 */
fun main(args: Array<String>) {

    fun buildFunctions(): MutableList<MathFunction> {
        return mutableListOf(
            object: MathFunction {
                override fun func(xParam: Double) = xParam.pow(2)
                override val description = "x^2"
            },
            object: MathFunction {
                override fun func(xParam: Double) = 1 / ln(xParam)
                override val description = "1/ln(x)"
            },
            object: MathFunction {
                override fun func(xParam: Double) = cos(xParam) / (xParam + 2)
                override val description = "cos(x)/(x+2)"
            },
            object: MathFunction {
                override fun func(xParam: Double) = sqrt(1 + 2 * xParam.pow(2) - xParam.pow(3))
                override val description = "sqrt(1 + 2x^2 - x^3)"
            }
        )
    }

    val keyReader = BufferedReader(InputStreamReader(System.`in`))
    val functions = buildFunctions()
    println("Введите номер желаемой функции:")
    functions.forEachIndexed { i, el -> println("${i}. ${el.description} dx") }
    /*
    Переменные funcNumber, limits, precision - необходимо инициализировать, так-как компилятор в строке 78 не может знать,
    что ветка when выполнилась, но это знаю я.
     */
    var funcNumber = -1
    var limits = listOf<Double>()
    var precision = 0.0
    keyReader.use {
        var inputStep = 0
        while (inputStep < 3) {
            when (inputStep) {
                0 -> {
                    try {
                        funcNumber = it.readLine().trim().toInt()
                        if (funcNumber in functions.indices) {
                            inputStep++
                            println("Введите пределы интегрирования через пробел:")
                        } else throw NumberFormatException()
                    } catch (e: NumberFormatException) { printError("Ошибка ввода: введите целое число в [1 ; 5].") }
                }
                1 -> {
                    try {
                        limits = it.readLine().trim().split(" ").map { n -> n.toDouble() }
                        if (limits.size == 2) {
                            inputStep++
                            println("Введите точность:")
                        } else throw NumberFormatException()
                    } catch (e: NumberFormatException) { printError("Ошибка ввода: введите два числа через пробел.") }
                }
                2 -> {
                    try {
                        precision = it.readLine().trim().toDouble()
                        if (precision in 0.000001..1.0) inputStep++ else throw NumberFormatException()
                    } catch (e: NumberFormatException) { printError("Ошибка ввода: введите число в [0.000001 ; 1].") }
                }
            }
        }
    }
    try {
        val answer = IntegralSolver.integrateByTrapezoid(functions[funcNumber], Limits(Pair(limits[0], limits[1])), precision)
        println("""Значение интеграла = ${answer.resValue}
                                    ~Количество разбиений = ${answer.blocks}
                                    ~Погрешность = ${answer.infelicity}""".trimMargin("~"))
    } catch (e: Exception) { println(e.message) }
}

var counter = 0
fun printError(msg: String) {
    counter++
    if (counter == 100) println("Ты чо дурак!? Дифиченто, блин!")
    System.err.println(msg)
}
package controllers

import Main
import math.Integral
import math.IntegralSolver
import math.Limits
import math.MathFunction
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.NumberFormatException

/**
 * Осуществляет взаимодействие с пользователем через эмулятор терминала.
 * @author Артемий Кульбако.
 */
fun launchConsole() {
    val keyReader = BufferedReader(InputStreamReader(System.`in`))
    println("Введите номер желаемой функции:")
    Main.functions.forEachIndexed { i, el -> println("${i}. $el dx") }
    var func: MathFunction? = null
    var limits: List<Double>? = null
    var precision: Double? = null
    keyReader.use {
        var inputStep = 0
        while (inputStep < 3) {
            when (inputStep) {
                0 -> {
                    try {
                        it.readLine().trim().toInt().let {n ->
                            if (n in Main.functions.indices) {
                                inputStep++
                                func = Main.functions[n]
                                println("Введите пределы интегрирования через пробел:")
                            } else throw NumberFormatException()
                        }
                    } catch (e: NumberFormatException) { System.err.println("Ошибка ввода: введите целое число в [1 ; 5].") }
                }
                1 -> {
                    try {
                        limits = it.readLine().trim().split(" ").map { n -> n.toDouble() }
                        if (limits!!.size == 2) {
                            inputStep++
                            println("Введите точность:")
                        } else throw NumberFormatException()
                    } catch (e: NumberFormatException) { System.err.println("Ошибка ввода: введите два числа через пробел.") }
                }
                2 -> {
                    try {
                        precision = it.readLine().trim().toDouble()
                        if (precision!! in 0.000001..1.0) inputStep++ else throw NumberFormatException()
                    } catch (e: NumberFormatException) { System.err.println("Ошибка ввода: введите число в [0.000001 ; 1].") }
                }
            }
        }
    }
    try {
        val integral = Integral(func!!, Limits(Pair(limits!![0], limits!![1]), precision!!))
        val answer = IntegralSolver.integrateByTrapezoid(integral, precision!!)
        println("""Значение интеграла = ${answer.resValue}
                ~Количество разбиений = ${answer.blocks}
                ~Погрешность = ${answer.infelicity}""".trimMargin("~"))
    } catch (e: Exception) { System.err.println(e.message) }
}
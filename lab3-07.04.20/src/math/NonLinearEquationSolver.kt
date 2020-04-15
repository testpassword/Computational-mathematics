package math

import kotlin.math.*

class NonLinearEquationSolver {

    enum class SolveMethods {
        BISECTION { override fun toString(): String { return "половинного деления" } },
        TANGENTS { override fun toString(): String { return "касательных" } },
        ITERATIVE { override fun toString(): String { return "итерационный" } }
    }

    companion object {

        private val MAX_ITERS = 100_000_000

        fun solve(system: List<MathFunction>, borders: Pair<Double, Double>, accuracy: Double,
                  method: SolveMethods): NonLinearEquationAnswer {
            if (borders.first > borders.second) throw IllegalArgumentException("Левая граница должна быть строго меньше правой")
            when (system.size) {
                1 -> if ((system[0].func(borders.first) * system[0].func(borders.second)) >= 0)
                    throw Exception("Не выполняется условие ƒ(a) * ƒ(b) < 0")
                else -> TODO("ПРОВЕРКА ДЛЯ СИСТЕМЫ")
            }
            return when (method) {
                SolveMethods.BISECTION -> bisectionMethod(system[0], borders, accuracy)
                SolveMethods.TANGENTS -> tangentsMethod(system[0], borders, accuracy)
                SolveMethods.ITERATIVE -> iterativeMethod(system, borders, accuracy)
            }
        }

        private fun bisectionMethod(function: MathFunction, borders: Pair<Double, Double>, accuracy: Double): NonLinearEquationAnswer {
            var left = borders.first
            var right = borders.second
            var x: Double
            var xFuncValue: Double
            var i = 0
            do {
                i++
                x = (left + right) / 2
                val leftFuncValue = function.func(left)
                xFuncValue = function.func(x)
                if (leftFuncValue * xFuncValue > 0) left = x else right = x
            } while (((right - left) > accuracy || abs(xFuncValue) > accuracy) && i <= MAX_ITERS)
            return NonLinearEquationAnswer(x, xFuncValue, i, i == MAX_ITERS)
        }

        private fun tangentsMethod(function: MathFunction, borders: Pair<Double, Double>, accuracy: Double): NonLinearEquationAnswer {
            fun findInitApproximation(function: MathFunction, borders: Pair<Double, Double>): Double {
                val a = function.func(borders.first) * findDerivative(function, borders.first, 2)
                val b = function.func(borders.first) * findDerivative(function, borders.first, 2)
                return when {
                    a > 0 -> a
                    b > 0 -> b
                    else -> (a + b) / 2
                }
            }

            var x = findInitApproximation(function, borders)
            var xFuncValue: Double
            var i = 0
            do {
                i++
                xFuncValue = function.func(x)
                val dX = findDerivative(function, x, 1)
                x -= xFuncValue / dX
            } while ((abs(xFuncValue) > accuracy) && i <= MAX_ITERS)
            return NonLinearEquationAnswer(x, xFuncValue, i, i == MAX_ITERS)
        }

        private fun iterativeMethod(system: List<MathFunction>, borders: Pair<Double, Double>, accuracy: Double): NonLinearEquationAnswer {
            TODO("Реализовать алгоритм")
            //return NonLinearEquationAnswer(0.0, 0.0, 0)
        }

        private fun findDerivative(f: MathFunction, x: Double, order: Int): Double {
            val h = 0.0001
            return when (order) {
                1 -> (f.func(x + h) - f.func(x - h)) / (2 * h)
                2 -> (f.func(x + h) - 2 * f.func(x) + f.func(x - h)) / h.pow(2)
                else -> throw IllegalArgumentException("Метод реализации производных этого порядка ещё не реализован")
            }
        }
    }
}

data class NonLinearEquationAnswer(val arg: Double, val funcValue: Double, val iterCounter: Int, val isCalcLimitReached: Boolean = false) {
    override fun toString(): String = """
        Ответ = $arg
        Значение функции в точке x = $funcValue 
        Итераций = $iterCounter
        """.trimIndent().plus(if (isCalcLimitReached) "\nБЫЛ ДОСТИГНУТ ЛИМИТ ВЫЧИСЛЕНИЙ" else "")
}
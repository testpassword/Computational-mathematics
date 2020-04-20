package math

import kotlin.math.*

internal class NonLinearEquationSolver {

    private val MAX_ITERS = 100_000_000

    internal fun bisectionMethod(f: MathFunction, borders: Pair<Double, Double>, accuracy: Double): NonLinearEquationAnswer {
        var left = borders.first
        var right = borders.second
        var x: Double
        var xFuncValue: Double
        var i = 0
        do {
            i++
            x = (left + right) / 2
            val leftFuncValue = f.func(left)
            xFuncValue = f.func(x)
            if (leftFuncValue * xFuncValue > 0) left = x else right = x
        } while (((right - left) > accuracy || abs(xFuncValue) > accuracy) && i <= MAX_ITERS)
        return NonLinearEquationAnswer(Pair(x, xFuncValue), i, i == MAX_ITERS)
    }

    internal fun tangentsMethod(f: MathFunction, borders: Pair<Double, Double>, accuracy: Double): NonLinearEquationAnswer {
        val left = f.func(borders.first) * findDerivative(f, borders.first, 2)
        val right = f.func(borders.first) * findDerivative(f, borders.first, 2)
        var x = when {
            left > 0 -> left
            right > 0 -> right
            else -> (left + right) / 2
        }
        var xFuncValue: Double
        var i = 0
        do {
            i++
            xFuncValue = f.func(x)
            val dX = findDerivative(f, x, 1)
            x -= xFuncValue / dX
        } while ((abs(xFuncValue) > accuracy) && i <= MAX_ITERS)
        return NonLinearEquationAnswer(Pair(x, xFuncValue), i, i == MAX_ITERS)
    }

    internal fun iterativeMethod(system: List<MathFunction>, borders: Pair<Double, Double>, accuracy: Double): NonLinearEquationAnswer {
        if (system.size == 1) {
            val derA = findDerivative(system[0], borders.first, 1)
            val derB = findDerivative(system[0], borders.second, 1)
            val maxDer = maxOf(derA, derB)
            if (maxDer >= 1) throw Exception("Не выполняется условие сходимости метода")
            var x = maxDer
            val lambda = -1 / maxDer
            var i = 0
            do {
                i++
                val previousX = x
                x += lambda * system[0].func(x)
            } while (abs(x - previousX) >= accuracy && i <= MAX_ITERS)
            return NonLinearEquationAnswer(Pair(x, system[0].func(x)), i, i == MAX_ITERS)
        } else {
            return NonLinearEquationAnswer(Pair(0.0, 0.0), 100, false)
        }
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

data class NonLinearEquationAnswer(val root: Pair<Double, Double>, val iterCounter: Int, val isCalcLimitReached: Boolean = false) {

    override fun toString() = """
        Ответ = ${root.first}
        Приближенное значение функции в точке x = ${root.second} 
        Итераций = $iterCounter
        """.trimIndent().plus(if (isCalcLimitReached) "\nБЫЛ ДОСТИГНУТ ЛИМИТ ВЫЧИСЛЕНИЙ" else "")
}
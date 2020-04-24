package math

import kotlin.math.*

/**
 * Предоставляет методы решения нелинейных уравнений.
 * @property MAX_ITERS максимальное число итераций.
 * @author Артемий Кульбако.
 * @version 1.6
 */
internal class NonLinearEquationSolver {

    var MAX_ITERS = 100_000_000

    /**
     * Решает нелинейное уравнение методом половинного деления.
     * @param f функция, значение которой необходимо вычислить.
     * @param borders интервал, на котором ищется корень.
     * @param accuracy точность вычислений.
     * @return результат вычислений.
     */
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

    /**
     * Решает нелинейное уравнение методом касательных.
     * @param f функция, значение которой необходимо вычислить.
     * @param borders интервал, на котором ищется корень.
     * @param accuracy точность вычислений.
     * @return результат вычислений.
     */
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

    /**
     * Решает систему нелинейных уравнение методом простых итераций. Может решить 1 или 2 уравнения.
     * @param system система функций, значения которых необходимо вычислить.
     * @param borders начальные приближения.
     * @param accuracy точность вычислений.
     * @return результат вычислений.
     * @throws IllegalCallerException если количество уравнений в системе меньше 1 или больше 2.
     * @throws Exception если не выполняется условие сходимости метода.
     */
    internal fun iterativeMethod(system: List<MathFunction>, borders: Pair<Double, Double>,
                                 accuracy: Double): NonLinearEquationAnswer {

        fun isAccuracyAchieve(oldX: DoubleArray, newX: DoubleArray) =
            oldX.zip(newX) { x, y -> abs(x - y) }.toDoubleArray().max()!! >= accuracy

        fun checkConvergence(vararg x: Double) =
            x.mapIndexed { j, it -> abs(findDerivative(system[j], it, 1)) }.max()!!.apply {
                if (this < 1) return this else throw Exception("Не выполняется условие сходимости метода")
            }

        var i = 0
        when (system.size) {
            1 -> {
                val derA = findDerivative(system[0], borders.first, 1)
                val derB = findDerivative(system[0], borders.second, 1)
                val maxDer = checkConvergence(derA, derB)
                var x = maxDer
                val lambda = -1 / maxDer
                do {
                    i++
                    val previousX = x
                    x += lambda * system[0].func(x)
                } while (abs(x - previousX) >= accuracy && i <= MAX_ITERS)
                return NonLinearEquationAnswer(Pair(x, system[0].func(x)), i, i == MAX_ITERS)
            }
            2 -> {
                var prevX: DoubleArray
                var newX = doubleArrayOf(borders.first, borders.second)
                do {
                    i++
                    prevX = newX.clone()
                    newX = doubleArrayOf(system[0].func(prevX[1]), system[1].func(prevX[0]))
                } while (isAccuracyAchieve(prevX, newX) && i <= MAX_ITERS)
                return NonLinearEquationAnswer(Pair(newX[0], newX[1]), i, i == MAX_ITERS)
            }
            else -> throw IllegalCallerException("Решение систем для более чем двух пока невозможно")
        }
    }

    /**
     * Вычисляет производную функции.
     * @param f дифференцируемая функция.
     * @param x точка дифференцирования.
     * @param order порядок производной.
     * @return результат дифференцирования в точке x.
     * @throws IllegalArgumentException если порядок производной меньше 1 или больше 2.
     */
    fun findDerivative(f: MathFunction, x: Double, order: Int): Double {
        val h = 0.0001
        return when (order) {
            1 -> (f.func(x + h) - f.func(x - h)) / (2 * h)
            2 -> (f.func(x + h) - 2 * f.func(x) + f.func(x - h)) / h.pow(2)
            else -> throw Exception("Метод расчёта производных этого порядка не реализован")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NonLinearEquationSolver) return false
        if (MAX_ITERS != other.MAX_ITERS) return false
        return true
    }

    override fun hashCode() = MAX_ITERS

    override fun toString() = "${this.javaClass.name}(MAX_ITERS = $MAX_ITERS)"
}

/**
 * Содержит результат решения системы нелинейных уравнений.
 * @property root координаты x, y корня системы.
 * @property iterCounter количество итераций выполненных в процессе нахождения корня.
 * @property isCalcLimitReached показывает, был ли достигнут максимальный лимит итераций. По-умолчанию = false.
 * @author Артемий Кульбако.
 * @version 1.3
 */
data class NonLinearEquationAnswer(val root: Pair<Double, Double>, val iterCounter: Int, val isCalcLimitReached: Boolean = false) {

    override fun toString() = """
        Ответ = ${root.first}
        Значение функции в точке x = ${root.second} 
        Итераций = $iterCounter
        """.trimIndent().plus(if (isCalcLimitReached) "\nБЫЛ ДОСТИГНУТ ЛИМИТ ВЫЧИСЛЕНИЙ" else "")
}
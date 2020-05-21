package math

import kotlin.math.*

/**
 * Предоставляет методы решения нелинейных уравнений.
 * @property MAX_ITERS максимальное число итераций.
 * @author Артемий Кульбако.
 * @version 1.6
 */
internal class NonLinearEquationSolver {

    var MAX_ITERS = 10_000_000

    /**
     * Решает нелинейное уравнение методом половинного деления.
     * @param f функция, значение которой необходимо вычислить.
     * @param borders интервал, на котором ищется корень.
     * @param accuracy точность вычислений.
     * @return результат вычислений.
     */
    internal fun bisectionMethod(f: MathFunction<Double>, borders: Pair<Double, Double>, accuracy: Double): NonLinearEquationAnswer {
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
        } while (((right - left) > accuracy || abs(xFuncValue) > accuracy) && i < MAX_ITERS)
        return NonLinearEquationAnswer(Pair(x, xFuncValue), i, i == MAX_ITERS)
    }

    /**
     * Решает нелинейное уравнение методом касательных.
     * @param f функция, значение которой необходимо вычислить.
     * @param borders интервал, на котором ищется корень.
     * @param accuracy точность вычислений.
     * @return результат вычислений.
     */
    internal fun tangentsMethod(f: MathFunction<Double>, borders: Pair<Double, Double>, accuracy: Double): NonLinearEquationAnswer {

        val firstApproach = {it: Double -> f.func(it) * f.findDerivative(it, 2) }
        val left = firstApproach(borders.first)
        val right = firstApproach(borders.second)
        var x = borders.toList().max()!!.let { if (it > 0 ) it else (left + right) / 2 }
        var xFuncValue: Double
        var i = 0
        do {
            i++
            xFuncValue = f.func(x)
            val dX = f.findDerivative(x, 1)
            x -= xFuncValue / dX
        } while ((abs(xFuncValue) > accuracy) && i < MAX_ITERS)
        return NonLinearEquationAnswer(Pair(x, xFuncValue), i, i == MAX_ITERS)
    }

    /**
     * Решает систему нелинейных уравнение методом простых итераций. Может решить 1 или 2 уравнения.
     * @param system система функций, значения которых необходимо вычислить.
     * @param borders начальные приближения.
     * @param accuracy точность вычислений.
     * @return результат вычислений.
     * @throws IllegalArgumentException если количество уравнений в системе меньше 1 или больше 2.
     * @throws Exception если не выполняется условие сходимости метода.
     */
    internal fun iterativeMethod(system: List<MathFunction<Double>>, borders: Pair<Double, Double>,
                                 accuracy: Double): NonLinearEquationAnswer {

        fun isAccuracyAchieve(oldX: DoubleArray, newX: DoubleArray) =
            oldX.zip(newX) { x, y -> abs(x - y) }.toDoubleArray().max()!! >= accuracy

        var i = 0
        when (system.size) {
            1 -> {
                val derA = system[0].findDerivative(borders.first, 1)
                val derB = system[0].findDerivative(borders.second, 1)
                val maxDer = sequenceOf(derA, derB).max()!!
                if (maxDer >= 1) throw Exception("Не выполняется условие сходимости метода")
                var x = maxDer
                val lambda = -1 / maxDer
                do {
                    i++
                    val previousX = x
                    x += lambda * system[0].func(x)
                } while (abs(x - previousX) >= accuracy && i < MAX_ITERS)
                return NonLinearEquationAnswer(Pair(x, system[0].func(x)), i, i == MAX_ITERS)
            }
            2 -> {
                var prevX: DoubleArray
                var newX = doubleArrayOf(borders.first, borders.second)
                do {
                    i++
                    prevX = newX.clone()
                    newX = doubleArrayOf(system[0].func(prevX[1]), system[1].func(prevX[0]))
                } while (isAccuracyAchieve(prevX, newX) && i < MAX_ITERS)
                return NonLinearEquationAnswer(Pair(newX[0], newX[1]), i, i == MAX_ITERS)
            }
            else -> throw IllegalArgumentException("Решение систем для более чем двух пока невозможно")
        }
    }

    /**
     * Вычисляет производную функции.
     * @param x точка дифференцирования.
     * @param order порядок производной.
     * @return результат дифференцирования в точке x.
     * @throws IllegalArgumentException если порядок производной меньше 1 или больше 2.
     */
    internal fun MathFunction<Double>.findDerivative(x: Double, order: Int): Double {
        val h = 0.0001
        return when (order) {
            1 -> (this.func(x + h) - this.func(x - h)) / (2 * h)
            2 -> (this.func(x + h) - 2 * this.func(x) + this.func(x - h)) / h.pow(2)
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
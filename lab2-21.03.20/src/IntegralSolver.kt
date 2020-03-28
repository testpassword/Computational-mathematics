import kotlin.math.abs

/**
 * Содержит результат решения интеграла.
 * @property resValue значение интеграла.
 * @property infelicity погрешность вычислений.
 * @property blocks количество разбиений.
 * @version 1.0
 */
data class IntegralAnswer(val resValue: Double, val infelicity: Double, val blocks: Int)

/**
 * Содержит результат пределы интегрирования.
 * @param limits верхний и нижний предел (не упорядоченные).
 * @property low нижний предел.
 * @property high верхний предел.
 * @property isSwitchedRange менялись ли пределы местами.
 * @version 1.0
 */
data class Limits(val limits: Pair<Double, Double>) {

    val low: Double
    val high: Double
    val isSwitchedRange: Boolean

    init {
        if (limits.first > limits.second) {
            this.low = limits.second
            this.high = limits.first
            this.isSwitchedRange = true
        } else {
            this.low = limits.first
            this.high = limits.second
            this.isSwitchedRange = false
        }
    }
}

/**
 * Находит численное значение интеграла.
 * @author Артемий Кульбако.
 * @version 1.0
 */
class IntegralSolver {

    companion object {
        /**
         * Находит значение интеграла методом трапеций.
         * @param mathFunc интеграл, который нужно посчитать.
         * @param limits пределы интеграла.
         * @param precision точность вычислений.
         * @return IntegralAnswer.
         * @version 1.0
         */
        fun integrateByTrapezoid(mathFunc: MathFunction, limits: Limits, precision: Double): IntegralAnswer {

            fun approximate(mathFunc: MathFunction, limits: Limits, step: Double): Double {
                var area = 0.0
                for (i in 0 until ((limits.high - limits.low) / step).toInt()) {
                    area += step * 0.5 * (mathFunc.func(limits.low + i * step) + (mathFunc.func(limits.low + (i + 1) * step)))
                    if (area.isNaN() || area.isInfinite()) throw Exception("Функция не определена на заданном отрезке.")
                }
                return area
            }
            var step = when {
                limits.low.let { it.isNaN() || it.isInfinite() } -> limits.high - (limits.low + precision)
                limits.high.let { it.isNaN() || it.isInfinite() } -> (limits.high - precision) - limits.low
                else -> limits.high - limits.low
            }
            var error: Double
            var integralN: Double
            var integral2N = approximate(mathFunc, limits, step)
            do {
                integralN = integral2N
                step /= 2
                integral2N = approximate(mathFunc, limits, step)
                error = calcError(integral2N, integralN)
            } while (error > precision)
            if (limits.isSwitchedRange) integral2N = - integral2N
            return IntegralAnswer(integral2N, error, ((limits.high - limits.low) / step).toInt())
        }

        private fun calcError(integralN: Double, integral2N: Double) = abs(integral2N - integralN) / 3
    }
}
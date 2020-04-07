package math

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
 * Находит численное значение интеграла разными методами.
 * @author Артемий Кульбако.
 * @version 1.0
 */
class IntegralSolver {

    /**
     * Константы, определяющие варианты решения методом прямоугольников.
     * @property LEFT метод левых прямоугольников.
     * @property CENTER метод средних прямоугольников.
     * @property RIGHT метод правых прямоугольников.
     */
    enum class RectangleMethodType { LEFT, CENTER, RIGHT }

    @FunctionalInterface
    private interface ApproximationRule { fun findValue(step: Double, i: Int): Double }

    companion object {

        /**
         * Находит значение интеграла методом трапеций.
         * @param integral интеграл, который нужно посчитать.
         * @param precision точность вычислений.
         * @return math.IntegralAnswer.
         */
        fun integrateByTrapezoid(integral: Integral, precision: Double): IntegralAnswer {
            val rule = object: ApproximationRule {
                override fun findValue(step: Double, i: Int) =
                    step * 0.5 * (integral.f.func(integral.limits.low + i * step) +
                            (integral.f.func(integral.limits.low + (i + 1) * step)))
            }
            return approximate(integral, precision, rule)
        }

        /**
         * Находит значение интеграла методом прямоугольников.
         * @param integral интеграл, который нужно посчитать.
         * @param precision точность вычислений.
         * @return math.IntegralAnswer.
         */
        fun integrateByRectangle(integral: Integral, precision: Double, type: RectangleMethodType): IntegralAnswer {
            val rule = when (type) {
                RectangleMethodType.LEFT -> object:
                    ApproximationRule {
                    override fun findValue(step: Double, i: Int) = step * integral.f.func(integral.limits.low + i * step)
                }
                RectangleMethodType.CENTER -> object:
                    ApproximationRule {
                    override fun findValue(step: Double, i: Int) =
                        (step * integral.f.func(integral.limits.low + i * step) +
                            step * integral.f.func(integral.limits.low + (i + 1) * step)) / 2
                }
                RectangleMethodType.RIGHT -> object:
                    ApproximationRule {
                    override fun findValue(step: Double, i: Int) = step * integral.f.func(integral.limits.low + (i + 1) * step)
                }
            }
            return approximate(integral, precision, rule)
        }

        private fun approximate(integral: Integral, precision: Double, rule: ApproximationRule): IntegralAnswer {
            fun findArea(integral: Integral, step: Double): Double {
                var area = 0.0
                for (i in 0 until ((integral.limits.high - integral.limits.low) / step).toInt()) {
                    area += rule.findValue(step, i)
                    if (area.isNaN() || area.isInfinite()) throw Exception("Функция не определена на заданном отрезке.")
                }
                return area
            }

            val limits = integral.limits
            var step = limits.high - limits.low
            var error: Double
            var integralN: Double
            var integral2N = findArea(integral, step)
            do {
                integralN = integral2N
                step /= 2
                integral2N = findArea(integral, step)
                error = calcError(integral2N, integralN)
            } while (error > precision)
            if (limits.isSwitchedRange) integral2N = - integral2N
            return IntegralAnswer(
                integral2N,
                error,
                ((limits.high - limits.low) / step).toInt()
            )
        }

        private fun calcError(integralN: Double, integral2N: Double) = abs(integral2N - integralN) / 3
    }
}
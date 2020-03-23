import kotlin.math.abs

/**
 * Класс, содержащий результат решения интеграла, согласно шаблону Data Model Object.
 * @property resValue значение интеграла.
 * @property infelicity погрешность вычислений.
 * @property blocks количество разбиений.
 * @version 0.2
 */
data class IntegralAnswer(val resValue: Double, val infelicity: Double, val blocks: Int)

/**
 * Находит численное значение интеграла.
 * @author Артемий Кульбако.
 * @version 0.1
 */
class IntegralSolver {

    companion object {
        /**
         * Находит значение интеграла методом трапеций.
         * @param mathFunc интеграл, который нужно посчитать.
         * @param limits пределы интеграла.
         * @param precision точность вычислений.
         * @return IntegralAnswer.
         * @version 0.3
         */
        fun integrateByTrapezoid(mathFunc: MathFunction, limits: Pair<Double, Double>, precision: Double): IntegralAnswer {
            if (limits.first == limits.second) return IntegralAnswer(0.0, 0.0, 1)
            val sortedLims = sortLimits(limits)
            var area = 0.0
            var blocks = 0
            //TODO: оценка по Рунге
            for (i in 0 until ((sortedLims.second - sortedLims.first) / precision).toInt()) {
                area += precision * 0.5 * (mathFunc.func(sortedLims.first + i * precision) + mathFunc.func(sortedLims.first + (i + 1) * precision))
                blocks++
            }
            return IntegralAnswer(area, 1.0, blocks)
        }

        private fun sortLimits(limits: Pair<Double, Double>): Pair<Double, Double> {
            return if (limits.first > limits.second) {
                Pair(limits.second, limits.first)
            } else {
                Pair(limits.first, limits.second)
            }
        }

        //TODO: переделать под интеграл
        /*fun isAccuracyReached(newX: DoubleArray, oldX: DoubleArray, precision: Double) =
            (newX.zip(oldX) { a, b -> abs(a - b) }.toDoubleArray().max()!! < precision)*/
    }
}
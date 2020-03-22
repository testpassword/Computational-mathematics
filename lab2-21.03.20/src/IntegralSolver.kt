/**
 * Класс, содержащий результат решения интеграла, согласно шаблону Data Model Object.
 * @property resValue значение интеграла.
 * @property infelicity погрешность вычислений.
 * @property blocks количество разбиений.
 * @version 1.0
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
         * @version 0.1
         */
        fun trapezoidMethod(mathFunc: Any, limits: Pair<Double, Double>, precision: Double): IntegralAnswer {
            //limits.first - нижний предел, limits.second - верхний
            if (limits.first == limits.second) return IntegralAnswer(0.0, 0.0, 1)
            val low: Double
            val high: Double
            if (limits.first > limits.second) {
                low = limits.second
                high = limits.first
            } else {
                low = limits.first
                high = limits.second
            }
            return IntegralAnswer(1.0, 1.0, 1)
        }

        private fun rateByRunge(): Double {
            return 0.0
        }
    }
}
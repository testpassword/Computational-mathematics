package math

/**
 * Представляет собой характеристику математической функции.
 * @property func осуществляет расчёт неизвестного параметра x по переданному правилу.
 * @version 1.3
 */
@FunctionalInterface
interface MathFunction {

    /**
     * @param x неизвестные переменные функций.
     */
    fun func(vararg x: Double): Double
}
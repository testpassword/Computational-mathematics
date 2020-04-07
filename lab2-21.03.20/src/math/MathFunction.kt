package math

/**
 * Представляет собой характеристику математической функции.
 * @property toString описание функции.
 * @property func осуществляет расчёт неизвестного параметр x по переданному правилу.
 * @version 1.0
 */
interface MathFunction {

    fun func(xParam: Double): Double

    override fun toString(): String
}
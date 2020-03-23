/**
 * Представляет собой характеристику математической функции.
 * @property description описание функции.
 * @property func осуществляет расчёт неизвестного параметр x по переданному правилу
 * @version 0.1
 */
interface MathFunction {

    fun func(xParameter: Double): Double

    val description: String
}
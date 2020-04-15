package math

/**
 * Представляет собой характеристику математической функции.
 * @property func осуществляет расчёт неизвестного параметра x по переданному правилу.
 * @version 1.1
 */
@FunctionalInterface
interface MathFunction { fun func(xParam: Double): Double }
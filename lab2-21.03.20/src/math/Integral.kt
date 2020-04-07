package math

import kotlin.math.abs

/**
 * Класс, содержащий функцию и пределы для интегрирования.
 * @property f функция.
 * @property limits пределы.
 * @version 1.0
 */
data class Integral(val f: MathFunction, val limits: Limits)

/**
 * Определяет пределы интегрирования для пары чисел.
 * @param limits пара неотсортированных чисел.
 * @param precision точность, на которую будут сдвинут предел, если он не определён.
 * @property low нижний предел.
 * @property high верхний предел.
 * @property isSwitchedRange менялись ли пределы местами.
 * @version 1.2
 */
data class Limits(val limits: Pair<Double, Double>, val precision: Double): Comparable<Limits> {

    val low: Double
    val high: Double
    val isSwitchedRange: Boolean
    val length: Double

    init {
        var low: Double
        var high: Double
        if (limits.first > limits.second) {
            low = limits.second
            high = limits.first
            this.isSwitchedRange = true
        } else {
            low = limits.first
            high = limits.second
            this.isSwitchedRange = false
        }
        this.low = if (low.let { it.isNaN() || it.isInfinite() }) (low + precision) else low
        this.high = if (high.let { it.isNaN() || it.isInfinite() }) (high - precision) else high
        this.length = abs(this.low) + abs(this.high)
    }

    /**
     * Сравнивает пределы по длине промежутка.
     * @param other другой предел.
     * @return результат сравнения.
     */
    override fun compareTo(other: Limits) = when {
        this.length < other.length -> -1
        this.length > other.length -> 1
        else -> 0
    }
}
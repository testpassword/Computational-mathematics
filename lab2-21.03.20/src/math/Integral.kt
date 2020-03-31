package math

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
 * @version 1.1
 */
data class Limits(val limits: Pair<Double, Double>, val precision: Double) {

    var low: Double
    var high: Double
    var isSwitchedRange: Boolean

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
        if (this.low.let { it.isNaN() || it.isInfinite() }) this.low += precision
        if (this.high.let { it.isNaN() || it.isInfinite() }) this.high -= precision
    }
}
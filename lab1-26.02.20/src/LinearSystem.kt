import java.lang.StringBuilder

/**
 * Структура данных для системы линейных алгебраических уравнений.
 * @property equations коэффиценты при уравнениях в системе.
 * @property resVector результирующий вектор системы.
 * @throws IllegalArgumentException если количество уравнений системы не совпадает с количеством ответов к ней.
 * @author Артемий Кульбако.
 */
class LinearSystem {

    var equations: Array<DoubleArray>
    var resVector: DoubleArray
    var size: Int
        private set

    constructor (equations: Array<DoubleArray>, resVector: DoubleArray) {
        if (equations.size == resVector.size) {
            this.equations = equations
            this.resVector = resVector
            this.size = resVector.size
        } else throw IllegalArgumentException("Количество уравнений и ответов к ним не совпадает.")
    }

    fun getDiagonalElements(): DoubleArray {
        val diags = mutableListOf<Double>()
        equations.forEachIndexed { i, el -> diags.add(el[i]) }
        return diags.toDoubleArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LinearSystem) return false
        if (!equations.contentDeepEquals(other.equations)) return false
        if (!resVector.contentEquals(other.resVector)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = equations.contentDeepHashCode()
        result = 31 * result + resVector.contentHashCode()
        return result
    }

    override fun toString(): String {
        val result = StringBuilder()
        equations.forEachIndexed { i, arr -> result.append("${arr.contentToString()}  *  x  = ${resVector[i]}\n") }
        return result.toString()
    }

}
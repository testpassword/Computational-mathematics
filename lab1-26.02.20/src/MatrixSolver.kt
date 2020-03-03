import kotlin.math.abs

/**
 * Находит столбец-вектор неизвестных для СЛАУ.
 * @author Артемий Кульбако.
 */
class MatrixSolver {

    companion object {
        /**
         * Решает СЛАУ методом Гаусса-Зейдаля.
         * @param linSys СЛАУ.
         * @param infelicity точность вычислений.
         */
        fun solveByGaussSeidel(linSys: LinearSystem, infelicity: Double): DoubleArray {
            toDiagonalPrevalence(linSys)
            //TODO: дальнейшая обработка матрицы
            return DoubleArray(0, { 0.0 })
        }

        private fun toDiagonalPrevalence(linSys: LinearSystem) {
            val coeffs = linSys.equations
            val r = coeffs.indices

            fun isDiagonalPrevalence(): Boolean {
                var condition1 = 0 //все эл. главной диагонали должно быть >= сумме модулей коэф. остальных ур-я
                var condition2 = false //хотя бы 1 из элементов должен быть > сумме модулей коэф. своего ур-я
                for (i in r) {
                    var diagEl = abs(coeffs[i][i])
                    var seriesSum = coeffs[i].sumByDouble { abs(it) } - diagEl
                    if (diagEl >= seriesSum) condition1++
                    if (diagEl > seriesSum) condition2 = true
                }
                return (condition1 == coeffs.size) && condition2
            }

            if (!isDiagonalPrevalence()) {
                val maxValuesIndices = mutableListOf<Int>()
                coeffs.forEach { maxValuesIndices.add(it.indexOf(it.maxBy { number -> abs(number) }!!)) }
                for (i in r)
                    for (j in r)
                        if (i != j)
                            if (maxValuesIndices[i] == maxValuesIndices[j]) throw Exception("Невозможно достичь  ̶д̶з̶е̶н̶ диагональное преобладание.")
                coeffs.sortBy { it.indexOf(it.maxBy { number -> abs(number) }!!) }
                val b = DoubleArray(linSys.size, { 0.0 })
                for (i in r) {
                    var strN = maxValuesIndices[i]
                    b[strN] = linSys.resVector[i]
                }
                linSys.resVector = b
            }
        }
    }
}
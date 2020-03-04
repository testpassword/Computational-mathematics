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
            linSys.let {
                if (!toDiagonalPrevalence(it)) throw Exception("Невозможно достичь  ̶д̶з̶е̶н̶ диагональное преобладание.")
                solve(it)
            }
            return DoubleArray(0, { 0.0 })
        }

        private fun toDiagonalPrevalence(linSys: LinearSystem): Boolean {

            fun isDiagonalPrevalence(matrix: Array<DoubleArray>): Boolean {
                var condition1 = 0 //все эл. главной диагонали должно быть >= сумме модулей коэф. остальных ур-я
                var condition2 = false //хотя бы 1 из элементов должен быть > сумме модулей коэф. своего ур-я
                matrix.forEachIndexed { i, el ->
                    var diagEl = abs(el[i])
                    var seriesSum = el.sumByDouble { abs(it) } - diagEl
                    if (diagEl >= seriesSum) condition1++
                    if (diagEl > seriesSum) condition2 = true
                }
                return (condition1 == matrix.size) && condition2
            }

            val coeffs = linSys.equations
            val r = coeffs.indices
            if (!isDiagonalPrevalence(coeffs)) {
                val maxValuesIndices = mutableListOf<Int>()
                coeffs.forEach { maxValuesIndices.add(it.indexOf(it.maxBy { number -> abs(number) }!!)) }
                for (i in r)
                    for (j in r)
                        if (i != j)
                            if (maxValuesIndices[i] == maxValuesIndices[j]) return false
                coeffs.sortBy { it.indexOf(it.maxBy { number -> abs(number) }!!) }
                val b = DoubleArray(linSys.resVector.size, { 0.0 })
                b.forEachIndexed { i, _ -> b[maxValuesIndices[i]] = linSys.resVector[i] }
                linSys.resVector = b
            }
            return true
        }

        private fun solve(linSys: LinearSystem) {
            val oldCoeffs = linSys.equations
            val size = oldCoeffs.size
            val approximation = DoubleArray(size, { 0.0 })
            val terms = linSys.resVector.zip(linSys.getDiagonalElements(), { a, b -> b / a })
            val tmpCoeffs = Array(size, { DoubleArray(size, { 0.0 }) })
            for (i in tmpCoeffs.indices) {
                for (j in tmpCoeffs.indices) {
                if (i == j) tmpCoeffs[i][j] = 0.0 else tmpCoeffs[i][j] = (-1) * oldCoeffs[i][j] / oldCoeffs[i][i]
                }
            }
            //TODO: остальное
        }
    }

}
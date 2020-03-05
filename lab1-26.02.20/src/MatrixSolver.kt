import kotlin.math.abs

/**
 * Находит столбец-вектор неизвестных для СЛАУ.
 * @author Артемий Кульбако.
 */
class MatrixSolver {

    companion object {
        /**
         * Решает СЛАУ методом Гаусса-Зейдаля.
         * @param linearSystem СЛАУ.
         * @param infelicity точность вычислений.
         * @param modify true - если разрешено изменять матрицу, false - если запрещено (работа с клоном).
         */
        fun solveByGaussSeidel(linearSystem: LinearSystem, infelicity: Double, modify: Boolean): DoubleArray {
            val linSys = if (modify) clone(linearSystem) else linearSystem
            linSys.let {
                if (!toDiagonalPrevalence(it)) throw Exception("Невозможно достичь  ̶д̶з̶е̶н̶ диагональное преобладание.")
                resolve(it)
                iterate(it)
            }
            return DoubleArray(0, { 0.0 })
        }

        private fun clone(linearSystem: LinearSystem) = LinearSystem(linearSystem.equations.map { it.clone() }.toTypedArray(), linearSystem.resVector.clone())

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

            val As = linSys.equations
            val r = As.indices
            if (!isDiagonalPrevalence(As)) {
                val maxValuesIndices = As.map { it.indexOf(it.maxBy { number -> abs(number) }!!) }
                for (i in r)
                    for (j in r)
                        if (i != j)
                            if (maxValuesIndices[i] == maxValuesIndices[j]) return false
                As.sortBy { it.indexOf(it.maxBy { number -> abs(number) }!!) }
                val Bs = linSys.resVector.clone()
                linSys.resVector.forEachIndexed { i, _ -> linSys.resVector[maxValuesIndices[i]] = Bs[i] }
            }
            return true
        }
        
        private fun resolve(linearSystem: LinearSystem) {
            val As = linearSystem.equations
            val size = As.size
            val approximations = DoubleArray(size, { 0.0 }) //x(0)
            linearSystem.resVector.zip(linearSystem.getDiagonalElements(), { a, b -> b / a }).toDoubleArray() //newB
            val tmpCoeffs = Array(size, { DoubleArray(size, { 0.0 }) }) //newA
            for (i in tmpCoeffs.indices)
                for (j in tmpCoeffs.indices)
                    tmpCoeffs[i][j] = if (i == j) 0.0 else (-1) * As[i][j] / As[i][i]
        }

        fun iterate(linearSystem: LinearSystem) {
            /*
            var iterCounter = 0
            do {
                var oldApproximations = approximations.clone()
                for (i in approximations.indices) {
                    var sum = getSum(i, approximations)
                    approximations[i] = newResVector[i] + sum
                }

                /*
                скопировать новые коэфф в старые коэфф
                посчитать новые коэфф
                 */
            } while ()

             */
        }
    }

}
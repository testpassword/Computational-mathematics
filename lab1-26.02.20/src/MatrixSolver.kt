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
        fun solveByGaussSeidel(linearSystem: LinearSystem, infelicity: Double, modify: Boolean): Pair<DoubleArray, Int> {
            val linSys = if (modify) clone(linearSystem) else linearSystem
            linSys.let {
                if (!toDiagonalPrevalence(it)) throw Exception("Невозможно достичь  ̶д̶з̶е̶н̶ диагональное преобладание.")
                transform(it)
                return iterate(it, infelicity)
            }
        }

        private fun clone(linSys: LinearSystem) =
            LinearSystem(linSys.equations.map { it.clone() }.toTypedArray(), linSys.resVector.clone())

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

        private fun transform(linSys: LinearSystem) {
            linSys.resVector = linSys.resVector.zip(linSys.getDiagonalElements(), { a, b -> a / b }).toDoubleArray()
            linSys.equations = linSys.equations.mapIndexed { i, doubles ->
                doubles.mapIndexed { j, d ->
                    if (i == j) 0.0 else (-1) * d / doubles[i]
                }.toDoubleArray()
            }.toTypedArray()
        }

        private fun iterate(linSys: LinearSystem, infelicity: Double): Pair<DoubleArray, Int> {

            fun isAccuracyReached(newX: DoubleArray, oldX: DoubleArray, precision: Double) =
                (newX.zip(oldX, { a, b -> abs(a - b) }).toDoubleArray().max()!! < precision)

            var iterCounter = 0
            var newApproximations = DoubleArray(linSys.size, { 0.0 }) //x(0)
            var oldApproximations: DoubleArray
            do {
                oldApproximations = newApproximations.clone()
                newApproximations.forEachIndexed { i, _ ->
                    var sum = 0.0
                    newApproximations.forEachIndexed { j, d -> sum += linSys.equations[i][j] * d }
                    newApproximations[i] = linSys.resVector[i] + sum
                }
                iterCounter++
            } while (!isAccuracyReached(newApproximations, oldApproximations, infelicity))
            return Pair(newApproximations, iterCounter)
        }
    }

}
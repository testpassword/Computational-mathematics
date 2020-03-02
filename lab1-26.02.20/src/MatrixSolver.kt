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
        fun solveByGaussSeidel(linSys: LinearSystem, infelicity: Double): MutableList<Double> {
            toDiagonalPrevalence(linSys)
            //TODO: дальнейшая обработка матрицы
            return mutableListOf<Double>()
        }

        private fun toDiagonalPrevalence(linSys: LinearSystem) {

            fun isDiagonalPrevalence(linSys: LinearSystem): Boolean {
                val coefficients = linSys.equations
                var condition1 = 0 //все эл. главной диагонали должно быть >= сумме модулей коэф. остальных ур-я
                var condition2 = false //хотя бы 1 из элементов должен быть > сумме модулей коэф. своего ур-я
                for (i in coefficients.indices) {
                    var diagEl = abs(coefficients[i][i])
                    var sum = coefficients[i].sumByDouble { abs(it) } - diagEl
                    if (diagEl >= sum) condition1++
                    if (diagEl > sum) condition2 = true
                }
                return (condition1 == coefficients.size) && condition2
            }

            if (isDiagonalPrevalence(linSys)) {
                //TODO: привести к диагональному преобладанию
            } else throw Exception("Невозможно достичь  ̶д̶з̶е̶н̶ диагональное преобладание.")
        }
    }
}
import kotlin.math.abs

/**
 * Функция-расширения, возвращающяя, диагональные элементы любой матрицы.
 * @return массив, состоящий из диагональных элементов матрицы.
 */
fun Array<DoubleArray>.getDiagonalElements() = mapIndexed { i, el -> el[i] }

/**
 * Класс, содержащий результат рещения СЛАУ, согласно шаблону Data Transfer Object.
 * @property xVector вектор незивестных.
 * @property infelicity столбец погрешностей вычислений.
 * @property count количество итераций, за которое метод нашёл решение.
 */
data class GaussSeidelAnswer(val xVector: DoubleArray, val infelicity: DoubleArray, val counter: Int)

/**
 * Находит столбец-вектор неизвестных для СЛАУ.
 * @author Артемий Кульбако.
 */
class LinearSystemSolver {

    companion object {
        /**
         * Решает СЛАУ методом Гаусса-Зейдаля.
         * @param linearSystem СЛАУ.
         * @param precision точность вычислений.
         * @param modify true - если разрешено изменять матрицу, false - если запрещено (работа с клоном).
         * @return GaussSeidelAnswer.
         * @throws Exception если невозможно привести матрицу к диагональному преобразованию.
         */
        fun solveByGaussSeidel(linearSystem: LinearSystem, precision: Double, modify: Boolean): GaussSeidelAnswer {
            val linSys = if (modify) clone(linearSystem) else linearSystem
            linSys.let {
                if (!toDiagonalPrevalence(it)) throw Exception("Невозможно достичь диагонального преобладания. Итерации расходятся.")
                transform(it)
                return iterate(it, precision)
            }
        }

        private fun clone(linSys: LinearSystem) =
            LinearSystem(linSys.equations.map { it.clone() }.toTypedArray(), linSys.resVector.clone())

        private fun toDiagonalPrevalence(linSys: LinearSystem): Boolean {

            fun isDiagonalPrevalence(matrix: Array<DoubleArray>): Boolean {
                var condition1 = 0 //все эл. главной диагонали должно быть >= сумме модулей коэф. остальных ур-я
                var condition2 = false //хотя бы 1 из элементов должен быть > сумме модулей коэф. своего ур-я
                matrix.forEachIndexed { i, el ->
                    val diagEl = abs(el[i])
                    val seriesSum = el.sumByDouble { abs(it) } - diagEl
                    if (diagEl >= seriesSum) condition1++
                    if (diagEl > seriesSum) condition2 = true
                }
                return (condition1 == matrix.size) && condition2
            }

            val A = linSys.equations
            val r = A.indices
            if (!isDiagonalPrevalence(A)) {
                val maxValuesIndices = A.map { it.indexOf(it.maxBy { number -> abs(number) }!!) }
                for (i in r)
                    for (j in r)
                        if (i != j)
                            if (maxValuesIndices[i] == maxValuesIndices[j]) return false
                A.sortBy { it.indexOf(it.maxBy { number -> abs(number) }!!) }
                val B = linSys.resVector.clone()
                linSys.resVector.forEachIndexed { i, _ -> linSys.resVector[maxValuesIndices[i]] = B[i] }
            }
            return true
        }

        private fun transform(linSys: LinearSystem) {
            linSys.resVector = linSys.resVector.zip(linSys.equations.getDiagonalElements()) { a, b -> a / b }.toDoubleArray()
            linSys.equations = linSys.equations.mapIndexed { i, doubles ->
                doubles.mapIndexed { j, d ->
                    if (i == j) 0.0 else (-1) * d / doubles[i]
                }.toDoubleArray()
            }.toTypedArray()
        }

        private fun iterate(linSys: LinearSystem, precision: Double): GaussSeidelAnswer {

            fun isAccuracyReached(newX: DoubleArray, oldX: DoubleArray, precision: Double) =
                (newX.zip(oldX) { a, b -> abs(a - b) }.toDoubleArray().max()!! < precision)

            var iterCounter = 0
            val newApproxes = DoubleArray(linSys.size) { 0.0 } //x(0) - approximations
            var oldApproxes: DoubleArray
            do {
                oldApproxes = newApproxes.clone()
                newApproxes.forEachIndexed { i, _ ->
                    newApproxes[i] = linSys.resVector[i] + newApproxes.mapIndexed { j, d ->
                        linSys.equations[i][j] * d }.sumByDouble { it } }
                iterCounter++
            } while (!isAccuracyReached(newApproxes, oldApproxes, precision))
            val infelicity = newApproxes.zip(oldApproxes) { a, b -> abs(a - b) }.toDoubleArray()
            return GaussSeidelAnswer(newApproxes, infelicity, iterCounter)
        }
    }

}
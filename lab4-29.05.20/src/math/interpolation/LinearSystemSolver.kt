package math.interpolation

import kotlin.math.abs

/**
 * @author [Автор метода](https://github.com/devtype-blogspot-com/Java-Examples/blob/master/SystemOfLinearEquations_Gaussian/src/Main.java)
 */
object LinearSystemSolver {

    fun gaussian(matrix: Array<DoubleArray>, resVector: DoubleArray): DoubleArray {
        val n = matrix.size
        for (p in 0 until n) {
            var max = p
            for (i in p + 1 until n)
                if (abs(matrix[i][p]) > abs(matrix[max][p])) max = i
            val temp = matrix[p]
            matrix[p] = matrix[max]
            matrix[max] = temp
            val t = resVector[p]
            resVector[p] = resVector[max]
            resVector[max] = t
            if (abs(matrix[p][p]) <= 1e-10) throw Exception("Нет решений или бесконечное множество решений")
            for (i in p + 1 until n) {
                val alpha = matrix[i][p] / matrix[p][p]
                resVector[i] -= alpha * resVector[p]
                for (j in p until n) matrix[i][j] -= alpha * matrix[p][j]
            }
        }
        // Обратный проход
        val x = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            var sum = 0.0
            for (j in i + 1 until n) sum += matrix[i][j] * x[j]
            x[i] = (resVector[i] - sum) / matrix[i][i]
        }
        return x
    }
}
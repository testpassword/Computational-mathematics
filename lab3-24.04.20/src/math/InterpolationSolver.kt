package math

class InterpolationSolver {

    /**
     * Возвращает аппроксимирующую функцию для заданного набора точек методом полинома Ньютона.
     * @param points набор точек по котором будет искаться функция.
     */
    internal fun newtonPolynomial(points: List<Point>): MathFunction<Double> {
        val order = points.size
        val finiteDiffs = Array(order) { DoubleArray(order) { 0.0 } }
        finiteDiffs[0] = points.map { it.y }.toDoubleArray() //Δy0 - нулевые конечные разности = значениям функции
        for (i in 1 until order)
            for (j in 0 until order - i)
                finiteDiffs[i][j] = finiteDiffs[i - 1][j + 1] - finiteDiffs[i - 1][j]
        val p0 = points[0]
        val h = points[1].x - p0.x
        return MathFunction<Double> { x ->
            var res = p0.y
            val q = (x[0]!! - p0.x) / h
            var product = 1.0
            for (i in 1 until order) {
                product = (product * (q + 1 - i)) / i
                res += product * finiteDiffs[i][0]
            }
            res
        }
    }
}
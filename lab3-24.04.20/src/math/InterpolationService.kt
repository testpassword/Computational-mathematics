package math

import math.InterpolationService.SolveMethods.*
import kotlin.math.E
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Предоставляет единую точку доступа к экземпляру класса {@see NonLinearEquationSolver}.
 * @property equations нелинейные уравнения с одной неизвестной переменной.
 * @author Артемий Кульбако.
 * @version 1.4
 */
object InterpolationService {

    /**
     * Константы, определяющие методы решения системы нелинейных уравнений.
     * @property NEWTON_POLYNOMIAL метод половинного деления.
     * @property LAGRANGE_POLYNOMIAL метод касательных (метод Ньютона).
     * @property CUBIC_SPLINE метод простых итераций.
     */
    enum class SolveMethods {
        NEWTON_POLYNOMIAL { override fun toString() = "Интерполирование многочленом Ньютона" },
        LAGRANGE_POLYNOMIAL { override fun toString() = "Интерполирование многочленом Лагранжа" },
        CUBIC_SPLINE { override fun toString() = "Интерполирование кубическими сплайнами" }
    }

    private val solver = InterpolationSolver()
    val equations: Set<MathFunction<Double>>

    init {
        equations = setOf(
            object: MathFunction<Double> {
                override fun func(vararg x: Double?) = -4 * (0.5 * x[0]!!).pow(4) + 4 * (0.5 * x[0]!!).pow(2)
                override fun toString() = "y = - 4 * (0.5 * x)^4 + 4 * (0.5 * x)^2"
            },
            object: MathFunction<Double> {
                override fun func(vararg x: Double?) = sin(x[0]!!)
                override fun toString() = "y = sin(x)"   //ПРОВЕРЕНА
            },
            object: MathFunction<Double> {
                override fun func(vararg x: Double?) = 1 / (1 + x[0]!!.pow(2))
                override fun toString() = "y = 1 / (1 + x^2)"
            },
            object: MathFunction<Double> {
                override fun func(vararg x: Double?) = x[0]!!.pow(3) * E.pow(x[0]!!)
                override fun toString() = "y = x^3 * e^x"
            }
        )
    }

    /**
     * Возвращает аппроксимирующую функцию для заданного набора точек.
     * @param method аппроксимирующий метод.
     * @param points набор точек по котором будет искаться функция.
     * @return аппроксимирующую функцию.
     */
    fun solve(method: SolveMethods, points: List<Point>) =
        when (method) {
            NEWTON_POLYNOMIAL -> solver.newtonPolynomial(points)
            else -> TODO("Метод не реализован")
        }

    /**
     * Генерирует n точек на на плоскости для переданного отрезка оси OX.
     * @param f функция, для вычисления точки y.
     * @param interval отрезок оси OX, на котором будут генерироваться точки.
     * @param n количество точек.
     * @return List<Point>.
     * @throws IllegalArgumentException если [interval.second <= interval.first || (n - 1) <= 0].
     * @throws ArithmeticException если генерируемая точка приняла значение [isNaN] или [isInfinite].
     */
    fun generateDots(f: MathFunction<Double>, interval: Pair<Double, Double>, n: Int): List<Point> {
        if (interval.second <= interval.first || (n - 1) <= 0)
            throw IllegalStateException("Левая граница должна быть строго больше правой; " +
                    "Количество точек должно быть больше одной")
        val step = (interval.second - interval.first) / (n - 1)
        return (0 until n).map {
            val x = interval.first + it * step
            if (x.isNaN() || x.isInfinite())
                throw ArithmeticException("Невозможно сгенерировать такое количество точек для заданного промежутка")
            Point(x, f.func(x))
        }.toList()
    }
}

/**
 * Точка на плоскости.
 * @param x координата x.
 * @param y координата y.
 */
data class Point(var x: Double, var y: Double): Comparable<Point> {

    private fun findHypotenuse() = sqrt(x.pow(2) + y.pow(2))

    override fun toString() = "( $x; $y )"

    override fun compareTo(other: Point) = (findHypotenuse() - other.findHypotenuse()).toInt()
}
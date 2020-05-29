package math

import Point
import math.InterpolationService.SolveMethods.*
import math.InterpolationSolver.LeastSquaresType
import kotlin.math.*

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
     * @property LEAST_SQUARES метод наименьших квадратов.
     */
    enum class SolveMethods {
        NEWTON_POLYNOMIAL { override fun toString() = "Интерполирование многочленом Ньютона" },
        LEAST_SQUARES { override fun toString() = "Аппроксимация методом наименьших квадратов" }
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
    fun solve(method: SolveMethods, points: List<Point>, type: LeastSquaresType?) =
        when (method) {
            NEWTON_POLYNOMIAL -> solver.newtonPolynomial(points)
            LEAST_SQUARES -> type!!.approximate(points)
        }

    /**
     * Генерирует [n] точек на на плоскости для переданного отрезка [interval] оси OX по правилу [f].
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
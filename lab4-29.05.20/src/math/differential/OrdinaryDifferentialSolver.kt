package math.differential

import math.MathFunction
import math.Point

/**
 * Содержит методы для решения обыкновенных дифференциальных уравнений.
 * @property EULER решение ОДУ методом Эйлера.
 * @author Артемий Кульбако.
 * @version 1.0
 */
internal enum class OrdinaryDifferentialSolver {

    EULER {
        override fun solve(f: MathFunction<Double>, startPoint: Point, h: Double, interval: ClosedFloatingPointRange<Double>) =
            generateSequence(startPoint) { Point(it.x + h, it.y + h * f.func(it.x, it.y)) }
                .takeWhile { it.x <= interval.endInclusive }.toList()

        override fun toString() = "Метод Эйлера"
    };

    /**
     * Решает обыкновенное дифференциальное уравнение. [f] - функция, для которой нужно точки ОДУ на промежутке [interval]
     * с шагом [h], где [startPoint] - x0 и y0.
     * @return набор точек, по которым можно построить дифференцированную функцию.
     */
    abstract fun solve(f: MathFunction<Double>, startPoint: Point, h: Double, interval: ClosedFloatingPointRange<Double>): List<Point>
}
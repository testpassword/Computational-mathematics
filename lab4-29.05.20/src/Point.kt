import kotlin.math.hypot

/**
 * Точка на плоскости.
 * @param x координата x.
 * @param y координата y.
 * @author Артемий Кульбако
 * @version 1.1
 */
data class Point(var x: Double, var y: Double): Comparable<Point> {

    override fun toString() = "( $x; $y )"

    override fun compareTo(other: Point) = (hypot(x, y) - hypot(other.x, other.y)).toInt()
}
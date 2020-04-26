package math

import kotlin.math.*

//TODO: проверить документацию
/**
 * Предоставляет единую точку доступа к экземпляру класса {@see NonLinearEquationSolver}.
 * @author Артемий Кульбако.
 * @version 1.4
 */
class MathFunctionService {

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

    /**
     * @property equations нелинейные уравнения с одной неизвестной переменной.
     */
    companion object {

        private val solver = InterpolationSolver()
        val equations: Set<MathFunction>

        init {
            equations = setOf(
                object : MathFunction {
                    override fun func(vararg x: Double) = x[0] / 2 - 3
                    override fun toString() = "x/2 - 3 = y"   //ПРОВЕРЕНА
                }
            )
        }

        fun solve() { }
    }
}
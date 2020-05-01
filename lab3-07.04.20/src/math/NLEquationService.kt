package math

import kotlin.math.*

/**
 * Предоставляет единую точку доступа к экземпляру класса {@see NonLinearEquationSolver}.
 * @author Артемий Кульбако.
 * @version 1.4
 */
class NLEquationService {

    /**
     * Константы, определяющие методы решения системы нелинейных уравнений.
     * @property BISECTION метод половинного деления.
     * @property TANGENTS метод касательных (метод Ньютона).
     * @property ITERATIVE метод простых итераций.
     */
    enum class SolveMethods {
        BISECTION { override fun toString() = "половинного деления" },
        TANGENTS { override fun toString() = "касательных" },
        ITERATIVE { override fun toString() = "итерационный" }
    }

    /**
     * @property equations нелинейные уравнения с одной неизвестной переменной.
     * @property sysOfEqsWithExpressedX уравнения для систем, где выражен X.
     * @property sysOfEqsWithExpressedY уравнения для систем, где выражен Y.
     */
    companion object {

        private val solver = NonLinearEquationSolver()
        val equations: Set<MathFunction>
        val sysOfEqsWithExpressedX: Set<MathFunction> //уравнение, где в системе выражен X
        val sysOfEqsWithExpressedY: Set<MathFunction> //уравнение, где в системе выражен Y

        init {
            this.equations = setOf(
                object: MathFunction {
                    override fun func(vararg x: Double) = x[0] / 2 - 3
                    override fun toString() = "x/2 - 3 = y"   //ПРОВЕРЕНА
                },
                object: MathFunction {
                    override fun func(vararg x: Double) = x[0].pow(3) - 0.2 * x[0].pow(2) + 0.5 * x[0] + 1.5
                    override fun toString() = "x^3 - 0.2x^2 +0.5x + 1.5 = y"   //ПРОВЕРЕНА
                },
                object: MathFunction {
                    override fun func(vararg x: Double) = (4 - Math.E.pow(2 * x[0])) / 3
                    override fun toString() = "(4 - e^(2x)) / 3 = y"   //ПРОВЕРЕНА
                },
                object: MathFunction {
                    override fun func(vararg x: Double) = sin(x[0])
                    override fun toString() = "sin(x) = y" //ПРОВЕРЕНА
                },
                object: MathFunction {
                    override fun func(vararg x: Double) = 7 - 5 * x[0]
                    override fun toString() = "5x + y = 7" //ПРОВЕРЕНА
                }
            )
            /*
            Для графика выражать одинаковые переменные (y), для решения системы - разные
            Системы уравнений идут наборами по 2
            //НЕ ЗАПУСКАТЬ! 1x с 3y; 2x с 1y
            */
            sysOfEqsWithExpressedX = setOf(
                object: MathFunction {
                    override fun func(vararg x: Double) = -0.5 + x[0]
                    override fun getPlotDot(vararg x: Double) = 0.5 + x[0]
                    override fun toString() = "x - y = -0.5"
                },
                object: MathFunction {
                    override fun func(vararg x: Double) = E.pow(x[0])
                    override fun getPlotDot(vararg x: Double) = ln(x[0])
                    override fun toString() = "x = e^y"
                }
            )
            sysOfEqsWithExpressedY = setOf(
                object: MathFunction {
                    override fun func(vararg x: Double) = 0.5 - cos(x[0])
                    override fun toString() = "y + cos(x) = 0.5"
                },
                object: MathFunction {
                    override fun func(vararg x: Double) = (x[0] + 2) / 6
                    override fun toString() = "6y - 2 = x"
                },
                object: MathFunction {
                    override fun func(vararg x: Double) = -0.3 * x[0].pow(3) - 1
                    override fun toString() = "-0.3x^3 - 1 = y"
                }
            )
        }

        /**
         * Определяет корректность существования корня на заданном промежутке, корректность заданного промежутка, и
         * определяет вызываемый для решения системы нелинейных уравнений метод.
         * @param system система уравнений, неизвестные которой нужно найти.
         * @param borders границы поиска.
         * @param accuracy точность вычислений.
         * @param method метод вычислений.
         * @return результат вычислений.
         * @throws Exception если не выполняется условие ƒ(a) * ƒ(b) < 0.
         */
        fun solve(system: List<MathFunction>, borders: Pair<Double, Double>, accuracy: Double,
                  method: SolveMethods): NonLinearEquationAnswer {
            if (borders.first > borders.second) throw IllegalArgumentException("Левая граница должна быть строго меньше правой")
            system.groupingBy { it }.eachCount().map { if (it.value > 1) throw Exception("Уравнения в системе должно быть различны") }
            if (system.size == 1 && system[0].func(borders.first) * system[0].func(borders.second) >= 0)
                throw Exception("Не выполняется условие ƒ(a) * ƒ(b) < 0")
            return when (method) {
                SolveMethods.BISECTION -> solver.bisectionMethod(system[0], borders, accuracy)
                SolveMethods.TANGENTS -> solver.tangentsMethod(system[0], borders, accuracy)
                SolveMethods.ITERATIVE -> solver.iterativeMethod(system, borders, accuracy)
            }
        }
    }
}
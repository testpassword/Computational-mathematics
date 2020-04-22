package math

import kotlin.math.*

//TODO: документация
class EquationService {

    enum class SolveMethods {
        BISECTION { override fun toString(): String { return "половинного деления" } },
        TANGENTS { override fun toString(): String { return "касательных" } },
        ITERATIVE { override fun toString(): String { return "итерационный" } }
    }

    companion object {

        private val solver = NonLinearEquationSolver()
        val equations: Set<MathFunction>
        val systemsOfEquations: Set<MathFunction>

        init {
            this.equations = setOf(
                object: MathFunction {
                    override fun func(vararg x: Double): Double = x[0] / 2 - 3
                    override fun toString() = "x/2 - 3 = y"   //ПРОВЕРЕНА
                },
                object: MathFunction {
                    override fun func(vararg x: Double): Double = x[0].pow(3) - 0.2 * x[0].pow(2) + 0.5 * x[0] + 1.5
                    override fun toString() = "x^3 - 0.2x^2 +0.5x + 1.5 = y"   //ПРОВЕРЕНА
                },
                object: MathFunction {
                    override fun func(vararg x: Double): Double = (4 - Math.E.pow(2 * x[0])) / 3
                    override fun toString() = "(4 - e^(2x)) / 3 = y"   //ПРОВЕРЕНА
                },
                object: MathFunction {
                    override fun func(vararg x: Double): Double = sin(x[0])
                    override fun toString() = "sin(x) = y" //ПРОВЕРЕНА
                }
            )
            this.systemsOfEquations = setOf(
                object: MathFunction {
                    override fun func(vararg x: Double) = x[0].pow(2)
                    override fun toString() = "x2 - x1^2 = 0"
                },
                object: MathFunction {
                    override fun func(vararg x: Double) = exp(x[0])
                    override fun toString() = "x2 - e^(x1) = 0"
                }
            )
        }

        fun solve(system: List<MathFunction>, borders: Pair<Double, Double>, accuracy: Double,
                  method: SolveMethods): NonLinearEquationAnswer {
            if (borders.first > borders.second) throw IllegalArgumentException("Левая граница должна быть строго меньше правой")
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
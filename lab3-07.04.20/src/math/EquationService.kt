package math

import kotlin.math.*

class EquationService {

    enum class SolveMethods {
        BISECTION { override fun toString(): String { return "половинного деления" } },
        TANGENTS { override fun toString(): String { return "касательных" } },
        ITERATIVE { override fun toString(): String { return "итерационный" } }
    }

    companion object {

        private val solver = NonLinearEquationSolver()
        val equations = setOf(
            object: MathFunction {
                override fun func(xParam: Double) = xParam / 2 - 3
                override fun toString() = "x/2 - 3"   //ПРОВЕРЕНА
            },
            object: MathFunction {
                override fun func(xParam: Double) = xParam.pow(3) - 0.2 * xParam.pow(2) + 0.5 * xParam + 1.5
                override fun toString() = "x^3 - 0.2x^2 +0.5x + 1.5"   //ПРОВЕРЕНА
            },
            object: MathFunction {
                override fun func(xParam: Double) = (4 - Math.E.pow(2 * xParam)) / 3
                override fun toString() = "(4 - e^(2x)) / 3"   //ПРОВЕРЕНА
            },
            object: MathFunction {
                override fun func(xParam: Double) = sin(xParam)
                override fun toString() = "sin(x)" //ПРОВЕРЕНА
            }
        )

        fun solve(system: List<MathFunction>, borders: Pair<Double, Double>, accuracy: Double,
                  method: SolveMethods): NonLinearEquationAnswer {
            if (borders.first > borders.second) throw IllegalArgumentException("Левая граница должна быть строго меньше правой")
            system.forEach {
                if ((it.func(borders.first) * it.func(borders.second)) >= 0) throw Exception("Не выполняется условие ƒ(a) * ƒ(b) < 0")
            }
            return when (method) {
                SolveMethods.BISECTION -> solver.bisectionMethod(system[0], borders, accuracy)
                SolveMethods.TANGENTS -> solver.tangentsMethod(system[0], borders, accuracy)
                SolveMethods.ITERATIVE -> solver.iterativeMethod(system, borders, accuracy)
            }
        }
    }
}
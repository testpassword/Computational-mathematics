package services

import math.MathFunction
import kotlin.math.*

class FunctionService {

    companion object {
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
    }
}
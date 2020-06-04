package math.differential

import math.MathFunction
import kotlin.math.pow
import kotlin.math.sin

object OrdinaryDifferentialService {

    val equations: List<MathFunction<Double>>

    init {
        //TODO: замена на другие
        equations = listOf(
            object: MathFunction<Double> {
                override fun func(vararg x: Double?) = 2 * x[0]!!.pow(2)
                override fun toString() = "y' = 2x^2"
            },
            object: MathFunction<Double> {
                override fun func(vararg x: Double?) = x[0]!! - x[1]!!
                override fun toString() = "y' = x - y"
            },
            object: MathFunction<Double> {
                override fun func(vararg x: Double?) = sin(x[0]!!) + x[1]!!
                override fun toString() = "y' = sin(x) + y"
            }
        )
    }
}
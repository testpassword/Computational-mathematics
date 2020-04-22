package controllers

import javafx.fxml.FXML
import javafx.scene.chart.*
import math.MathFunction

class GraphController {

    @FXML private lateinit var canvas: LineChart<Double, Double>

    fun drawGraph(equation: MathFunction, borders: Pair<Double, Double> = Pair(-5.0, 5.0), precision: Int = 666) {
        val series = XYChart.Series<Double, Double>()
        var a = borders.first
        val b = borders.second
        while (a <= b) {
            series.data.add(XYChart.Data(a, equation.func(a)))
            a += (borders.second - borders.first) / precision
        }
        (canvas.xAxis as NumberAxis).apply {
            this.lowerBound = borders.first
            this.upperBound = borders.second
            this.isAutoRanging = false
        }
        (canvas.yAxis as NumberAxis).apply {
            this.lowerBound = borders.first
            this.upperBound = borders.second
            this.isAutoRanging = false
        }
        canvas.data.add(series)
    }

    fun clear() = this.canvas.data.clear()

    fun drawPoint(x: Double, y: Double) = canvas.data.add(XYChart.Series<Double, Double>().apply { this.data.add(XYChart.Data(x, y)) })
}
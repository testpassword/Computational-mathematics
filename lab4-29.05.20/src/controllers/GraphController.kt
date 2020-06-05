package controllers

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Point2D
import javafx.scene.chart.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import math.MathFunction
import java.net.URL
import java.util.*

/**
 * Управляет графиком приложения, представляющим из себя экземпляр JavaFX-класса [LineChart].
 * @author Артемий Кульбако.
 * @version 1.0
 */
class GraphController: Initializable {

    @FXML private lateinit var canvas: LineChart<Double, Double>

    /**
     * Инициализирует экземпляр класс [GraphController], устанавливая его стиль отображения.
     * @param url адрес внешнего ресурса, который может быть загружен.
     * @param bundle набор загруженных внешний ресурс, который может быть использован.
     */
    override fun initialize(url: URL?, bundle: ResourceBundle?) { this.canvas.createSymbols = false }

    /**
     * Рисует на графике функцию [equation] на промежутке [interval]: первое число - нижний предел по x и y, второе -
     * верхний предел по x и y. По-умолчанию равны [-5; 5]. [precision] количество точек, по которым будет построен график.
     * Для прямых линий не играет роли, но может быть важен для ломаных. По-умолчанию = 666.
     */
    fun drawLine(equation: MathFunction<Double>, interval: Pair<Double, Double> = Pair(-10.0, 10.0), precision: Int = 666) {
        val (a, b) = interval
        val h = (b - a) / precision
        arrayOf(canvas.xAxis, canvas.yAxis).map {
            (it as NumberAxis).let { axis ->
                axis.lowerBound = a
                axis.upperBound = b
            }
        }
        canvas.data.add(
            XYChart.Series<Double, Double>().apply {
                this.data.addAll(generateSequence(a) { it + h }.takeWhile { it <= b }.map { XYChart.Data(it, equation.getPlotDot(it)) }.toList())
                this.name = equation.toString()
            })
    }

    /** Уничтожает старое изображение на графике. */
    fun clear() = this.canvas.data.clear()

    /** Рисует точку [p] на графике цветом [color] (по-умолчанию [Color.RED]). */
    fun drawPoint(p: Point2D, color: Color = Color.RED) =
        this.canvas.data.add(XYChart.Series<Double, Double>().apply {
            this.data.add(XYChart.Data(p.x, p.y).apply {
                this.node = Circle(3.0, color)
            })
        })
}
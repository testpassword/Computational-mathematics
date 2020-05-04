package controllers

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.chart.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import math.MathFunction
import java.net.URL
import java.util.*

/**
 * Управляет графиком приложения, представляющим из себя экземпляр класса {@see LineChart}.
 * интерфейса.
 * @author Артемий Кульбако.
 * @version 1.0
 */
class GraphController: Initializable {

    @FXML private lateinit var canvas: LineChart<Double, Double>

    /**
     * Инициализирует экземпляр класс GraphController, устанавливая его стиль отображения.
     * @param url адрес внешнего ресурса, который может быть загружен.
     * @param bundle загруженный внешний ресурс, который может быть использован.
     */
    override fun initialize(url: URL?, bundle: ResourceBundle?) { this.canvas.createSymbols = false }

    /**
     * Рисует на графике линию для переданной математической функции.
     * @param equation функция, график которой нужно нарисовать.
     * @param borders границы, между которыми будет нарисована функция. Первое число - нижний предел по x и y, второе -
     * верхний предел по x и y. По-умолчанию равны [-5; 5].
     * @param precision количество точек, по которым будет построен график. Для прямых линий не играет роли, но может
     * быть важен для ломаных. По-умолчанию = 666.
     */
    fun drawLine(equation: MathFunction<Double>, borders: Pair<Double, Double> = Pair(-5.0, 5.0), precision: Int = 666) {
        val series = XYChart.Series<Double, Double>()
        var a = borders.first
        val b = borders.second
        while (a <= b) {
            series.data.add(XYChart.Data(a, equation.getPlotDot(a)))
            a += (borders.second - borders.first) / precision
        }
        sequenceOf(canvas.xAxis, canvas.yAxis).map {
            (it as NumberAxis).let { axis ->
                axis.lowerBound = borders.first
                axis.upperBound = borders.second
            }
        }
        canvas.data.add(series)
    }

    /** Уничтожает старое изображение на графике. */
    fun clear() = this.canvas.data.clear()

    /**
     * Рисует точку на графике.
     * @param x координаты точки.
     * @param y координата точки.
     * @param color цвет точки. По-умолчанию {@see Color.RED}.
     */
    fun drawPoint(x: Double, y: Double, color: Color = Color.RED) = this.canvas.data
        .add(XYChart.Series<Double, Double>().apply { this.data.add(XYChart.Data(x,y).apply { this.node = Circle(3.0, color) }) })
}
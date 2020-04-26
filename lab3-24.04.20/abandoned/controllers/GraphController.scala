package controllers

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.chart.{LineChart, NumberAxis, XYChart}
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import math.MathFunction

/**
 * Управляет графиком приложения, представляющим из себя экземпляр класса {@see LineChart}.
 * интерфейса.
 * @author Артемий Кульбако.
 * @version 1.0
 */
class GraphController extends Initializable {

  @FXML private var canvas: LineChart[Double, Double] = _

  /**
   * Инициализирует экземпляр класс GraphController, устанавливая его стиль отображения.
   * @param url адрес внешнего ресурса, который может быть загружен.
   * @param resourceBundle загруженный внешний ресурс, который может быть использован.
   */
  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = { this.canvas.setCreateSymbols(false) }

  /**
   * Рисует на графике линию для переданной математической функции.
   * @param equation функция, график которой нужно нарисовать.
   * @param borders границы, между которыми будет нарисована функция. Первое число - нижний предел по x и y, второе -
   * верхний предел по x и y. По-умолчанию равны [-5; 5].
   * @param precision количество точек, по которым будет построен график. Для прямых линий не играет роли, но может
   * быть важен для ломаных. По-умолчанию = 666.
   */
  def drawLine(equation: MathFunction, borders: (Double, Double) = (-5, 5), precision: Int = 666): Unit = {
    def setRanges(axis: NumberAxis): Unit = {
      axis.setLowerBound(borders._1)
      axis.setUpperBound(borders._2)
      axis.setAutoRanging(false)
    }

    val series = new XYChart.Series[Double, Double]()
    var a = borders._1
    val b = borders._2
    while (a <= b) {
      series.getData.add(new XYChart.Data[Double, Double](a, equation.getPlotDot(a)))
      a += (borders._2 - borders._1) / precision
    }
    Array(canvas.getXAxis, canvas.getYAxis).map(_.asInstanceOf[NumberAxis]).foreach(setRanges)
  }

  /** Уничтожает старое изображение на графике. */
  def clear(): Unit = this.canvas.getData.clear()

  /**
   * Рисует точку на графике.
   * @param x координаты точки.
   * @param y координата точки.
   * @param color цвет точки. По-умолчанию {@see Color.RED}.
   */
  def drawPoint(x: Double, y: Double, color: Color = Color.RED): Unit = {
    val series = new XYChart.Series[Double, Double]()
    val dot = new XYChart.Data(x, y)
    dot.setNode(new Circle(3, color))
    series.getData.add(dot)
    this.canvas.getData.add(series)
  }
}
package math

/**
 * Представляет собой характеристику математической функции.
 * @author Артемий Кульбако.
 * @version 1.3
 */
trait MathFunction {

  /**
   * Вычисляет значение функции.
   * @param x неизвестные переменные функции.
   */
  def func(x: Double*): Double

  /**
   * Вычисляет точку для отрисовки на графике для функции. По-умолчанию вызывает метод func(varargs x: Double)
   * для функций вида y = x, но необходимо переопределяет для функций с несколькими неизвестными переменными.
   * @param x неизвестные переменные функции.
   */
  def getPlotDot(x: Double*): Double = func(x:_*)
}
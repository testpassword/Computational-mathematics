package controllers

import AeroMain
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Cursor
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.*
import javafx.scene.effect.DropShadow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import math.MathFunction
import math.NonLinearEquationSolver
import math.NonLinearEquationSolver.SolveMethods
import services.FunctionService
import java.awt.Desktop
import java.net.URI
import javax.naming.SizeLimitExceededException

class GUIController {

    @FXML private lateinit var toolbar: HBox
    @FXML private lateinit var radioBtn: RadioButton
    @FXML private lateinit var eq1Chooser: ComboBox<MathFunction>
    @FXML private lateinit var eq2Chooser: ComboBox<MathFunction>
    @FXML private lateinit var methodChooser: ComboBox<SolveMethods>
    @FXML private lateinit var leftBoundLbl: Label
    @FXML private lateinit var rightBoundLbl: Label
    @FXML private lateinit var outputArea: TextArea
    @FXML private lateinit var leftBoundInput: TextField
    @FXML private lateinit var rightBoundInput: TextField
    @FXML private lateinit var infelicityInput: TextField
    @FXML private lateinit var canvas: LineChart<Double, Double>
    private val RED_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.RED)
    private val BLUE_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.DEEPSKYBLUE)
    private val GREEN_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.LIGHTGREEN)
    private val eqsMethods = FXCollections.observableArrayList(SolveMethods.BISECTION, SolveMethods.TANGENTS)
    private val sysOfEqsMethod = FXCollections.observableArrayList(SolveMethods.ITERATIVE)

    fun initialize() {
        toolbar.let {
            it.onMousePressed = EventHandler { mouseEvent: MouseEvent -> it.cursor = Cursor.MOVE }
            it.onMouseEntered = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { it.cursor = Cursor.HAND } }
            it.onMouseExited = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { toolbar.cursor = Cursor.DEFAULT } }
        }
        arrayOf(eq1Chooser, eq2Chooser, methodChooser, leftBoundInput, rightBoundInput, infelicityInput, radioBtn).forEach {
            it.focusedProperty().addListener { _, _, newValue -> it.effect = if (newValue!!) BLUE_LIGHT else null }
        }
        arrayOf(eq1Chooser, eq2Chooser).forEach { it.items = FXCollections.observableArrayList(FunctionService.equations) }
        methodChooser.items = eqsMethods
    }

    @FXML private fun minimizeWindow() { AeroMain.stage.isIconified = true }

    @FXML private fun closeProgram() = Platform.exit()

    @FXML private fun showCredits() = Desktop.getDesktop().browse(URI("https://github.com/testpassword"))

    @FXML private fun onRadioChanged() {
        if (radioBtn.isSelected) {
            eq2Chooser.isDisable = false
            methodChooser.items = sysOfEqsMethod
            leftBoundLbl.text = "начальное приближение x1"
            rightBoundLbl.text = "начальное приближение x2"
        } else {
            eq2Chooser.isDisable = true
            methodChooser.items = eqsMethods
            leftBoundLbl.text = "левая граница"
            rightBoundLbl.text = "правая граница"
        }
        methodChooser.value = null
    }

    @FXML private fun showResult() {
        try {
            val selectedEqs = arrayOf(eq1Chooser, eq2Chooser).mapNotNull { it.value }
            val borders = arrayOf(leftBoundInput, rightBoundInput).map { it.text.toDouble() }.zipWithNext()[0].apply {
                if (this.first > this.second) throw IllegalArgumentException()
                val MAX = Double.MAX_VALUE / 2
                if (this.first > MAX || this.second > MAX) throw SizeLimitExceededException()
            }
            val accuracy = infelicityInput.text.toDouble().apply { if (this < 0.000001) throw NumberFormatException() }
            val res = NonLinearEquationSolver.solve(selectedEqs, borders, accuracy, methodChooser.value)
            canvas.data.clear()
            selectedEqs.forEach { drawGraph(it, borders, res.iterCounter) }
            drawPoint(res.arg, res.funcValue)
            outputArea.appendText(res.toString())
            outputArea.effect = GREEN_LIGHT
        } catch (e: Exception) {
            when (e) {
                is NumberFormatException -> outputArea.appendText("""
                    ОШИБКА!
                    Поля должны быть представлены числом
                    максимально допустимая точность 0.000001
                """.trimIndent())
                is IllegalArgumentException -> outputArea.appendText("Левая граница должна быть строго меньше правой")
                is IllegalStateException -> outputArea.appendText("Метод решения не выбран")
                is SizeLimitExceededException -> outputArea.appendText("Значения не должны превышать ${Double.MAX_VALUE / 2}")
                else -> outputArea.appendText(e.localizedMessage)
            }
            outputArea.effect = RED_LIGHT
        } finally { outputArea.appendText("\n") }
    }

    fun drawGraph(equation: MathFunction, borders: Pair<Double, Double>, precision: Int) {
        val series = Series<Double, Double>()
        var a = borders.first
        var b = borders.second
        while (a <= b) {
            series.data.add(XYChart.Data(a, equation.func(a)))
            a += (borders.second - borders.first) / precision
        }
        canvas.data.add(series)
    }

    fun drawPoint(x: Double, y: Double) = canvas.data.add(Series<Double, Double>().apply { this.data.add(XYChart.Data(x, y)) })
}
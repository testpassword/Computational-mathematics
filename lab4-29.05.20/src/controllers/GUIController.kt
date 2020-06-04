package controllers

import AeroMain
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.effect.DropShadow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import math.*
import math.differential.OrdinaryDifferentialService
import math.differential.OrdinaryDifferentialSolver
import math.interpolation.InterpolationSolver
import java.awt.Desktop
import java.net.*
import java.util.*
import kotlin.Exception

/**
 * Управляет взаимодействием с пользователем посредством графического интерфейса.
 * @property RED_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @property GREEN_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @property BLUE_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @author Артемий Кульбако.
 * @version 1.0
 */
class GUIController: Initializable {

    val RED_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.RED)
    val BLUE_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.DEEPSKYBLUE)
    val GREEN_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.LIGHTGREEN)
    @FXML private lateinit var toolbar: HBox
    @FXML private lateinit var gControl: GraphController
    @FXML private lateinit var methodChooser: ComboBox<OrdinaryDifferentialSolver>
    @FXML private lateinit var funcChooser: ComboBox<MathFunction<Double>>
    @FXML private lateinit var leftBoundInput: TextField
    @FXML private lateinit var rightBoundInput: TextField
    @FXML private lateinit var leftBoundValInput: TextField
    @FXML private lateinit var accInput: TextField
    @FXML private lateinit var outputArea: TextArea
    @FXML private lateinit var mainPane: BorderPane
    @FXML private lateinit var interpolationControl: HBox
    @FXML private lateinit var xInput: TextField
    @FXML private lateinit var yOutput: TextField
    private lateinit var method: OrdinaryDifferentialSolver
    private lateinit var func: MathFunction<Double>
    private lateinit var approxFunc: MathFunction<Double>
    private var dots: List<Point>? = null
    companion object { private var linesCounter = 1 }

    /**
     * Инициализирует экземпляр класс GUIController, навешивая на различные его элементы обработчики событий и эффекты
     * постобработки.
     * @param url адрес внешнего ресурса, который может быть загружен.
     * @param bundle внешний ресурс, который может быть использован.
     */
    override fun initialize(url: URL?, bundle: ResourceBundle?) {
        toolbar.let {
            it.onMousePressed = EventHandler { mouseEvent: MouseEvent -> it.cursor = Cursor.MOVE }
            it.onMouseEntered = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { it.cursor = Cursor.HAND } }
            it.onMouseExited = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { toolbar.cursor = Cursor.DEFAULT } }
        }
        sequenceOf(methodChooser, funcChooser, leftBoundInput, rightBoundInput, leftBoundValInput, accInput,
            xInput, yOutput).forEach {
            it.focusedProperty().addListener { _, _, newVal -> it.effect = if (newVal!!) BLUE_LIGHT else null }
        }
        val resetDots = {
            dots?.let {
                dots = null
                printMessage(BLUE_LIGHT, "Точки сброшены, так как параметры функции были изменены")
            }
            interpolationControl.isDisable = true
        }
        sequenceOf(methodChooser, funcChooser, leftBoundInput, rightBoundInput, leftBoundValInput, accInput).forEach {
            when (it) {
                is ComboBox<out Any> -> it.valueProperty().addListener {_, _, _ -> resetDots() }
                is TextField -> it.textProperty().addListener {_, _, _ -> resetDots() }
            }
        }
        sequenceOf(leftBoundInput, rightBoundInput, leftBoundValInput, xInput).forEach {
            it.textProperty().addListener{ _, oldVal, newVal ->
                if (!newVal.matches(Regex("-?\\d{0,2}([.]\\d{0,6})?"))) {
                    printMessage(RED_LIGHT, "Поле должны быть представлены числом", "Максимальная точность - 6 знаков после запятой")
                    println(oldVal)
                    it.text = oldVal
                }
            }
        }
        xInput.textProperty().addListener { _, oldVal, newVal ->
            val x = newVal.toDouble()
            val min = dots!!.min()!!.x
            val max = dots!!.max()!!.x
            if (x in min..max) {
                val y = approxFunc.func(x)
                yOutput.text = y.toString()
                val p = Point(x, y)
                redrawGraph(Pair(min, max), p)
            } else {
                printMessage(RED_LIGHT, "x должно быть в интервале [${leftBoundInput.text} ; ${rightBoundInput.text}]")
                xInput.text = oldVal
            }
        }
        methodChooser.valueProperty().addListener {
                _, _, _ -> method = methodChooser.value }
        funcChooser.valueProperty().addListener {
                _, _, _ -> func = funcChooser.value }
        //TODO: методы и уравнения
        funcChooser.items = FXCollections.observableList(OrdinaryDifferentialService.equations)
        methodChooser.items = FXCollections.observableList(OrdinaryDifferentialSolver.values().toList())
        FXMLLoader(javaClass.getResource("/resources/graph.fxml")).let {
            mainPane.right = it.load()
            gControl = it.getController() as GraphController
        }
    }

    /** Минимизирует окно программы.*/
    @FXML fun minimizeWindow() { AeroMain.stage!!.isIconified = true }

    /** Завершает работу программы.*/
    @FXML fun closeProgram() = Platform.exit()

    /** Открывает профиль разработчика на github через стандартный веб-браузер в системе.*/
    @FXML fun showCredits() = Desktop.getDesktop().browse(URI("https://github.com/testpassword"))

    /** Печатает сообщение в главное окно вывода. */
    @FXML fun printMessage(lightning: DropShadow, vararg strings: String) {
        strings.forEach {
            outputArea.appendText("$linesCounter. $it\n")
            linesCounter++
        }
        outputArea.effect = lightning
    }

    private fun redrawGraph(borders: Pair<Double, Double>, extraPoint: Point? = null) =
        gControl.let {
            it.clear()
            //it.drawLine(this.func, borders)
            it.drawLine(this.approxFunc, borders)
            dots!!.forEach {p -> it.drawPoint(p) }
            if (extraPoint != null) gControl.drawPoint(extraPoint, Color.DARKKHAKI)
        }

    @FXML private fun showResult() {
        try {
            val x0 = leftBoundInput.text.toDouble()
            val xn = rightBoundInput.text.toDouble()
            val y0 = leftBoundValInput.text.toDouble()
            if (x0 >= xn) throw Exception("x0 =$x0 должно быть меньше xn=$xn")
            val accuracy = accInput.text.toDouble().let {
                if (it <= 0) {
                    printMessage(RED_LIGHT, "Точность должна быть строго больше 0", "Мы поправили её за вас 🥰")
                    it.coerceAtLeast(0.01)
                } else it
            }
            if (((xn - x0) / accuracy) > 50) throw Exception("Сильно много точек, JavaFX не поймёт 😭")
            this.dots = OrdinaryDifferentialSolver.EULER.solve(this.func, Point(x0, y0), accuracy, (x0..xn))
            this.approxFunc = InterpolationSolver.newtonPolynomial(this.dots!!)
            redrawGraph(Pair(x0, xn))
            //TODO: разблокировать расчёт в другой точке
        } catch (e: Exception) { printMessage(RED_LIGHT, e.localizedMessage) }
    }
}
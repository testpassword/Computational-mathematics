package controllers

import AeroMain
import Point
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.effect.DropShadow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.util.converter.DoubleStringConverter
import math.InterpolationService
import math.InterpolationService.SolveMethods
import math.InterpolationSolver.LeastSquaresType
import math.MathFunction
import java.awt.Desktop
import java.net.URI
import java.net.URL
import java.util.*

/**
 * Управляет взаимодействием с пользователем посредством графического интерфейса.
 * @property RED_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @property GREEN_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @property BLUE_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @author Артемий Кульбако.
 * @version 1.2
 */
class GUIController: Initializable {

    @FXML private lateinit var toolbar: HBox
    @FXML private lateinit var gControl: GraphController
    @FXML private lateinit var methodChooser: ComboBox<SolveMethods>
    @FXML private lateinit var funcChooser: ComboBox<MathFunction<Double>>
    @FXML private lateinit var leftBoundInput: TextField
    @FXML private lateinit var rightBoundInput: TextField
    @FXML private lateinit var outputArea: TextArea
    @FXML private lateinit var pointsAmountFld: TextField
    @FXML private lateinit var pointsTable: TableView<Point>
    @FXML private lateinit var mainPane: BorderPane
    @FXML private lateinit var interpolationControl: HBox
    @FXML private lateinit var xCol: TableColumn<Point, Double>
    @FXML private lateinit var yCol: TableColumn<Point, Double>
    @FXML private lateinit var xInput: TextField
    @FXML private lateinit var yOutput: TextField
    @FXML private lateinit var approxTypeChooser: ComboBox<LeastSquaresType>
    private val fxMethods = FXCollections.observableArrayList(SolveMethods.values().toList())
    private val fxEqs = FXCollections.observableArrayList(InterpolationService.equations)
    private val fxApproxTypes = FXCollections.observableArrayList(LeastSquaresType.values().toList())
    val RED_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.RED)
    val BLUE_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.DEEPSKYBLUE)
    val GREEN_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.LIGHTGREEN)
    private var func: MathFunction<Double>? = null
    private var approxFunc: MathFunction<Double>? = null
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
        sequenceOf(methodChooser, funcChooser, leftBoundInput, rightBoundInput, pointsAmountFld,
            pointsTable, xInput, yOutput).forEach {
            it.focusedProperty().addListener { _, _, newVal -> it.effect = if (newVal!!) BLUE_LIGHT else null }
        }
        val resetDots = {
            dots?.let {
                dots = null
                printMessage(BLUE_LIGHT, "Точки сброшены, так как параметры функции были изменены")
            }
            pointsTable.items = null
            interpolationControl.isDisable = true
        }
        methodChooser.valueProperty().addListener {_, _, newVal ->
            approxTypeChooser.isDisable = newVal != SolveMethods.LEAST_SQUARES
        }
        sequenceOf(methodChooser, funcChooser, leftBoundInput, rightBoundInput, pointsAmountFld).forEach {
            when (it) {
                is ComboBox<out Any> -> it.valueProperty().addListener {_, _, _ -> resetDots() }
                is TextField -> it.textProperty().addListener {_, _, _ -> resetDots() }
            }
        }
        sequenceOf(leftBoundInput, rightBoundInput, xInput).forEach {
            it.textProperty().addListener{ _, oldVal, newVal ->
                if (!newVal.matches(Regex("-?\\d{0,2}([.]\\d{0,6})?"))) {
                    printMessage(RED_LIGHT, "Поле должны быть представлены числом", "Максимальная точность - 6 знаков после запятой")
                    it.text = oldVal
                }
            }
        }
        pointsAmountFld.textProperty().addListener{ _, oldVal, newVal ->
            if (!newVal.matches(Regex("^$|([1-9]|1[013])$"))) {
                printMessage(RED_LIGHT, "Поле должны быть представлено числом от 1 до 13")
                pointsAmountFld.text = oldVal
            }
        }
        xCol.cellValueFactory = PropertyValueFactory("x")
        yCol.let {
            it.cellValueFactory = PropertyValueFactory("y")
            it.cellFactory = TextFieldTableCell.forTableColumn(DoubleStringConverter())
            it.setOnEditCommit { event ->
                if (!event.newValue.toString().matches(Regex("-?\\d{0,2}([.]\\d{0,2})?")))
                    printMessage(RED_LIGHT, "Поле должны быть представлены числом", "Максимальная точность - 2 знака")
                else {
                    (event.tableView.items[event.tablePosition.row]).y = event.newValue
                    interpolationControl.isDisable = true
                }
            }
        }
        xInput.textProperty().addListener { _, oldVal, newVal ->
            val x = newVal.toDouble()
            if (x <= dots!!.max()!!.x) {
                val y = approxFunc!!.func(x)
                yOutput.text = y.toString()
                val p = Point(x, y)
                redrawGraph(Pair(dots!!.min()!!.x, dots!!.max()!!.x), p)
            } else {
                printMessage(RED_LIGHT, "x не должно быть больше правого значения интервала")
                pointsAmountFld.text = oldVal
            }
        }
        methodChooser.items = fxMethods
        funcChooser.items = fxEqs
        approxTypeChooser.items = fxApproxTypes
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

    @FXML private fun redrawGraph(borders: Pair<Double, Double>, extraPoints: Point? = null) =
        gControl.let {
            it.clear()
            it.drawLine(this.func!!, borders)
            it.drawLine(this.approxFunc!!, borders)
            dots!!.forEach {p -> it.drawPoint(p) }
            if (extraPoints != null) gControl.drawPoint(extraPoints, Color.DARKKHAKI)
        }

    @FXML private fun showResult() {
        try {
            val method = methodChooser.value
            this.func = funcChooser.value
            val borders = arrayOf(leftBoundInput, rightBoundInput).map { it.text.toDouble() }.zipWithNext()[0]
            val n = pointsAmountFld.text.toInt()
            if (this.dots == null) this.dots = InterpolationService.generateDots(this.func!!, borders, n)
            pointsTable.items = FXCollections.observableList(dots)
            this.approxFunc = if (approxTypeChooser.isDisabled) {
                InterpolationService.solve(method, dots!!, null)
            } else InterpolationService.solve(method, dots!!, approxTypeChooser.value)
            redrawGraph(borders)
            interpolationControl.isDisable = false
            printMessage(GREEN_LIGHT, "Аппроксимирующая функция получена")
        } catch (e: Exception) {
            when (e) {
                is NumberFormatException -> printMessage(RED_LIGHT, "Одно из полей не заполнено или заполнено неверно",
                    "Читай подсказки при вводе данных")
                else -> printMessage(RED_LIGHT, e.localizedMessage)
            }
        }
    }
}
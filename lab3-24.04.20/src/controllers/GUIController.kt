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
import javafx.scene.layout.*
import javafx.scene.paint.Color
import math.MathFunction
import math.MathFunctionService
import math.MathFunctionService.SolveMethods
import java.awt.Desktop
import java.lang.Exception
import java.net.*
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
    @FXML private lateinit var funcChooser: ComboBox<MathFunction>
    @FXML private lateinit var leftBoundInput: TextField
    @FXML private lateinit var rightBoundInput: TextField
    @FXML private lateinit var outputArea: TextArea
    @FXML private lateinit var pointsAmountFld: TextField
    @FXML private lateinit var pointsTable: TableView<Any>
    @FXML private lateinit var graphPlaceholder: VBox
    private val fxMethods = FXCollections.observableArrayList(SolveMethods.values().toList())
    private val fxEqs = FXCollections.observableArrayList(MathFunctionService.equations)
    val RED_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.RED)
    val BLUE_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.DEEPSKYBLUE)
    val GREEN_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.LIGHTGREEN)
    companion object { private var linesCounter = 1 }

    /**
     * Инициализирует экземпляр класс GUIController, навешивая на различные его элементы обработчики событий и эффекты
     * постобработки.
     * @param url адрес внешнего ресурса, который может быть загружен.
     * @param bundle загруженный внешний ресурс, который может быть использован.
     */
    override fun initialize(url: URL?, bundle: ResourceBundle?) {
        toolbar.let {
            it.onMousePressed = EventHandler { mouseEvent: MouseEvent -> it.cursor = Cursor.MOVE }
            it.onMouseEntered = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { it.cursor = Cursor.HAND } }
            it.onMouseExited = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { toolbar.cursor = Cursor.DEFAULT } }
        }
        sequenceOf(methodChooser, funcChooser, methodChooser, leftBoundInput, rightBoundInput, pointsAmountFld,
            pointsTable).forEach {
            it.focusedProperty().addListener { _, _, newValue -> it.effect = if (newValue!!) BLUE_LIGHT else null }
        }
        sequenceOf(leftBoundInput, rightBoundInput).forEach {
            it.textProperty().addListener{ _, oldValue, newValue ->
                if (!newValue.matches(Regex("-?\\d{0,2}([.]\\d{0,6})?"))) {
                    printMessage(RED_LIGHT, "Поля должны быть представлены числом", "Максимальная точность - 6 знаков после запятой")
                    it.text = oldValue
                }
            }
        }
        pointsAmountFld.textProperty().addListener{ _, oldValue, newValue ->
            if (!newValue.matches(Regex("^$|([1-9]|1[013])$"))) {
                printMessage(RED_LIGHT, "Поля должны быть представлены числом", "Максимальная точность - 6 знаков после запятой")
                pointsAmountFld.text = oldValue
            }
        }
        methodChooser.items = fxMethods
        funcChooser.items = fxEqs
        FXMLLoader(javaClass.getResource("/resources/graph.fxml")).let {
            graphPlaceholder.children.add(it.load())
            gControl = it.getController() as GraphController
        }
    }

    /** Минимизирует окно программы.*/
    @FXML fun minimizeWindow() { AeroMain.stage.isIconified = true }

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

    @FXML private fun showResult() {
        try {
            val method = methodChooser.value
            val f = funcChooser.value
            val borders = arrayOf(leftBoundInput, rightBoundInput).map { it.text.toDouble() }.zipWithNext()[0]
            val n = pointsAmountFld.text.toInt()
            val res = MathFunctionService.solve()
            gControl.clear()
            gControl.drawLine(f, borders)
            /*
            Нарисовать точки
            Заполнить таблицу x и y, количество строк = n

            Пользователь может подправить точки в таблице, и сгенерировать график заново
            После генер. доступа вычисление в любой
            После подправления заблокировать выч. в любой точке
             */
        } catch (e: Exception) {
            when (e) {
                is NumberFormatException -> printMessage(RED_LIGHT, "Одно из полей не заполнено или заполнено неверно",
                    "Читай подсказки при вводе данных")
                else -> printMessage(RED_LIGHT, e.localizedMessage)
            }
        }
    }
}
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
import math.MathFunctionService
import math.MathFunctionService.SolveMethods
import math.MathFunction
import java.awt.Desktop
import java.net.*
import java.util.*
import javax.naming.SizeLimitExceededException

/**
 * Управляет взаимодействием с пользователем посредством графического интерфейса.
 * @property RED_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @property GREEN_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @property BLUE_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @author Артемий Кульбако.
 * @version 1.1
 */
class GUIController: Initializable {

    @FXML private lateinit var toolbar: HBox
    @FXML private lateinit var systemSwitch: RadioButton
    @FXML private lateinit var eq1Chooser: ComboBox<MathFunction>
    @FXML private lateinit var eq2Chooser: ComboBox<MathFunction>
    @FXML private lateinit var methodChooser: ComboBox<SolveMethods>
    @FXML private lateinit var allMethodsSwitch: RadioButton
    @FXML private lateinit var leftBoundLbl: Label
    @FXML private lateinit var rightBoundLbl: Label
    @FXML private lateinit var outputArea: TextArea
    @FXML private lateinit var leftBoundInput: TextField
    @FXML private lateinit var rightBoundInput: TextField
    @FXML private lateinit var infelicityInput: TextField
    @FXML private lateinit var mainPane: BorderPane
    private val fxMethods = FXCollections.observableArrayList(SolveMethods.values().toList())
    private val fxSysMethods = FXCollections.observableArrayList(SolveMethods.ITERATIVE)
    private lateinit var gControl: GraphController
    private val fxEqs = FXCollections.observableArrayList(MathFunctionService.equations)
    private val fxSysOfEqsX = FXCollections.observableArrayList(MathFunctionService.sysOfEqsWithExpressedX)
    private val fxSysOfEqsY = FXCollections.observableArrayList(MathFunctionService.sysOfEqsWithExpressedY)
    val RED_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.RED)
    val BLUE_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.DEEPSKYBLUE)
    val GREEN_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.LIGHTGREEN)

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
        sequenceOf(eq1Chooser, eq2Chooser, methodChooser, leftBoundInput, rightBoundInput, infelicityInput,
            systemSwitch, allMethodsSwitch).forEach {
            it.focusedProperty().addListener { _, _, newValue -> it.effect = if (newValue!!) BLUE_LIGHT else null }
        }
        eq1Chooser.items = fxEqs
        methodChooser.items = fxMethods
        FXMLLoader(javaClass.getResource("/resources/graph.fxml")).apply {
            mainPane.right = this.load()
            gControl = this.getController() as GraphController
        }
    }

    /** Минимизирует окно программы.*/
    @FXML fun minimizeWindow() { AeroMain.stage.isIconified = true }

    /** Завершает работу программы.*/
    @FXML fun closeProgram() = Platform.exit()

    /** Открывает профиль разработчика на github через стандартный веб-браузер в системе.*/
    @FXML fun showCredits() = Desktop.getDesktop().browse(URI("https://github.com/testpassword"))

    @FXML private fun systemSwitchChanged() {
        if (systemSwitch.isSelected) {
            eq2Chooser.isDisable = false
            eq1Chooser.items = fxSysOfEqsX
            eq2Chooser.items = fxSysOfEqsY
            methodChooser.items = fxSysMethods
            leftBoundLbl.text = "x1(0)"
            rightBoundLbl.text = "x2(0)"
            allMethodsSwitch.isDisable = true
        } else {
            eq2Chooser.isDisable = true
            eq1Chooser.items = fxEqs
            methodChooser.items = fxMethods
            leftBoundLbl.text = "левая гр."
            rightBoundLbl.text = "правая гр."
            allMethodsSwitch.isDisable = false
        }
        arrayOf(eq1Chooser, eq2Chooser).forEach { it.value = null }
        methodChooser.value = null
    }

    @FXML private fun allMethodsSwitchChanged() { methodChooser.isDisable = allMethodsSwitch.isSelected }

    @FXML private fun showResult() {
        val MAX = Double.MAX_VALUE / 2
        try {
            val selectedEqs = arrayOf(eq1Chooser, eq2Chooser).mapNotNull { it.value }
            val borders = arrayOf(leftBoundInput, rightBoundInput).map { it.text.toDouble() }.zipWithNext()[0].apply {
                if (this.first > this.second) throw IllegalArgumentException()
                if (this.first > MAX || this.second > MAX) throw SizeLimitExceededException()
            }
            val accuracy = infelicityInput.text.toDouble().apply { if (this < 0.000001) throw NumberFormatException() }
            val res = if (allMethodsSwitch.isSelected)
                SolveMethods.values().map { MathFunctionService.solve(selectedEqs, borders, accuracy, it) }.toTypedArray()
            else arrayOf(MathFunctionService.solve(selectedEqs, borders, accuracy, methodChooser.value))
            gControl.clear()
            if (selectedEqs.size == 1) gControl.drawLine(selectedEqs[0], borders) else selectedEqs.forEach { gControl.drawLine(it) }
            res.forEach {
                gControl.drawPoint(it.root.first, it.root.second)
                outputArea.appendText("$it\n")
            }
            outputArea.effect = GREEN_LIGHT
        } catch (e: Exception) {
            when (e) {
                is NumberFormatException -> outputArea.appendText("""
                    Поля должны быть представлены числом
                    максимально допустимая точность 0.000001
                """.trimIndent())
                is IllegalArgumentException -> outputArea.appendText("Левая граница должна быть строго меньше правой")
                is IllegalStateException -> outputArea.appendText("Метод решения не выбран")
                is SizeLimitExceededException -> outputArea.appendText("Значения не должны превышать $MAX")
                else -> outputArea.appendText(e.localizedMessage)
            }
            outputArea.effect = RED_LIGHT
        } finally { outputArea.appendText("\n") }
    }
}
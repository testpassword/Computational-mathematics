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
import math.EquationService
import math.EquationService.SolveMethods
import math.MathFunction
import java.awt.Desktop
import java.net.*
import java.util.*
import javax.naming.SizeLimitExceededException

class GUIController: Initializable {

    @FXML private lateinit var toolbar: HBox
    @FXML private lateinit var radioBtn: RadioButton
    @FXML private lateinit var eq1Chooser: ComboBox<MathFunction>
    @FXML private lateinit var eq2Chooser: ComboBox<MathFunction>
    private lateinit var eqChoosers: Array<ComboBox<MathFunction>>
    @FXML private lateinit var methodChooser: ComboBox<SolveMethods>
    @FXML private lateinit var leftBoundLbl: Label
    @FXML private lateinit var rightBoundLbl: Label
    @FXML private lateinit var outputArea: TextArea
    @FXML private lateinit var leftBoundInput: TextField
    @FXML private lateinit var rightBoundInput: TextField
    @FXML private lateinit var infelicityInput: TextField
    @FXML private lateinit var mainPane: BorderPane
    private val RED_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.RED)
    private val BLUE_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.DEEPSKYBLUE)
    private val GREEN_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.LIGHTGREEN)
    private val eqsMethods = FXCollections.observableArrayList(SolveMethods.values().toList())
    private val sysOfEqsMethod = FXCollections.observableArrayList(SolveMethods.ITERATIVE)
    private lateinit var gControl: GraphController
    val fxEqs = FXCollections.observableArrayList(EquationService.equations)
    val fxSysOfEqs = FXCollections.observableArrayList(EquationService.systemsOfEquations)

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        toolbar.let {
            it.onMousePressed = EventHandler { mouseEvent: MouseEvent -> it.cursor = Cursor.MOVE }
            it.onMouseEntered = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { it.cursor = Cursor.HAND } }
            it.onMouseExited = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { toolbar.cursor = Cursor.DEFAULT } }
        }
        arrayOf(eq1Chooser, eq2Chooser, methodChooser, leftBoundInput, rightBoundInput, infelicityInput, radioBtn).forEach {
            it.focusedProperty().addListener { _, _, newValue -> it.effect = if (newValue!!) BLUE_LIGHT else null }
        }
        eqChoosers = arrayOf(eq1Chooser, eq2Chooser).apply { this.forEach { it.items = fxEqs } }
        methodChooser.items = eqsMethods
        FXMLLoader(javaClass.getResource("/resources/graph.fxml")).apply {
            mainPane.right = this.load()
            gControl = this.getController() as GraphController
        }
    }

    @FXML private fun minimizeWindow() { AeroMain.stage.isIconified = true }

    @FXML private fun closeProgram() = Platform.exit()

    @FXML private fun showCredits() = Desktop.getDesktop().browse(URI("https://github.com/testpassword"))

    @FXML private fun onRadioChanged() {
        if (radioBtn.isSelected) {
            eq2Chooser.isDisable = false
            eqChoosers.forEach { it.items = this.fxSysOfEqs }
            methodChooser.items = sysOfEqsMethod
            leftBoundLbl.text = "x1(0)"
            rightBoundLbl.text = "x2(0)"
        } else {
            eq2Chooser.isDisable = true
            eqChoosers.forEach { it.items = this.fxEqs }
            methodChooser.items = eqsMethods
            leftBoundLbl.text = "левая гр."
            rightBoundLbl.text = "правая гр."
        }
        arrayOf(eq1Chooser, eq2Chooser).forEach { it.value = null }
        methodChooser.value = null
    }

    @FXML private fun showResult() {
        val MAX = Double.MAX_VALUE / 2
        try {
            val selectedEqs = arrayOf(eq1Chooser, eq2Chooser).mapNotNull { it.value }
            val borders = arrayOf(leftBoundInput, rightBoundInput).map { it.text.toDouble() }.zipWithNext()[0].apply {
                if (this.first > this.second) throw IllegalArgumentException()
                if (this.first > MAX || this.second > MAX) throw SizeLimitExceededException()
            }
            val accuracy = infelicityInput.text.toDouble().apply { if (this < 0.000001) throw NumberFormatException() }
            val res = EquationService.solve(selectedEqs, borders, accuracy, methodChooser.value)
            gControl.clear()
            if (selectedEqs.size == 1) gControl.drawGraph(selectedEqs[0], borders) else selectedEqs.forEach { gControl.drawGraph(it) }
            gControl.drawPoint(res.root.first, res.root.second)
            outputArea.appendText(res.toString())
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
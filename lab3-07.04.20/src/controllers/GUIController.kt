package controllers

import AeroMain
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.effect.DropShadow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import java.awt.Desktop
import java.net.URI

class GUIController {

    @FXML private lateinit var toolbar: HBox
    @FXML private lateinit var calcBtn: Button
    @FXML private lateinit var eq1Chooser: ComboBox<String>
    @FXML private lateinit var eq2Chooser: ComboBox<String>
    @FXML private lateinit var methodChooser: ComboBox<String>
    @FXML private lateinit var outputArea: TextArea
    @FXML private lateinit var leftBoundInput: TextField
    @FXML private lateinit var rightBoundInput: TextField
    @FXML private lateinit var infelicityInput: TextField
    private var eqSysEnable = false
    private val RED_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.RED)
    private val BLUE_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.DEEPSKYBLUE)
    private val GREEN_LIGHT = DropShadow(25.0, 0.0, 0.0, Color.LIGHTGREEN)
    private val equations = FXCollections.observableArrayList("Деление пополам", "Касательных")
    private val sysOfEquations = FXCollections.observableArrayList("Простых итераций")

    fun initialize() {
        toolbar.let {
            it.onMousePressed = EventHandler { mouseEvent: MouseEvent -> it.cursor = Cursor.MOVE }
            it.onMouseEntered = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { it.cursor = Cursor.HAND } }
            it.onMouseExited = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { toolbar.cursor = Cursor.DEFAULT } }
        }
        setOf(leftBoundInput, rightBoundInput, infelicityInput).forEach {
            it.focusedProperty().addListener { _, _, newValue -> it.effect = if (newValue!!) BLUE_LIGHT else null }
        }
        methodChooser.items = equations
        //TODO: заполнить комбобоксы уравнениями и методами
    }

    @FXML private fun minimizeWindow() { AeroMain.stage.isIconified = true }

    @FXML private fun closeProgram() = Platform.exit()

    @FXML private fun showCredits() = Desktop.getDesktop().browse(URI("https://github.com/testpassword"))

    @FXML private fun onRadioChanged() {
        if (!eqSysEnable) {
            eq2Chooser.isDisable = false
            methodChooser.items = sysOfEquations
        } else {
            eq2Chooser.isDisable = true
            methodChooser.items = equations
        }
        eqSysEnable = eqSysEnable.not()
    }

    @FXML private fun calculate() {
        try {
            outputArea.appendText("Робит\n")
            val light = DropShadow(25.0, 0.0, 0.0, Color.RED)
            outputArea.effect = GREEN_LIGHT
        } catch (e: Exception) {
            outputArea.appendText(e.localizedMessage)
            outputArea.effect = RED_LIGHT
        }
    }
}
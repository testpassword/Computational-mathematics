package controllers

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import math.*
import java.lang.*
import math.IntegralSolver
import math.IntegralSolver.RectangleMethodType
import kotlin.Exception

/**
 * Управляет взаимодействием с пользователем через графический интерфейс.
 * @author Артемий Кульбако
 * @version 1.2
 */
class GUIController {

    private enum class SolveMethods {
        TRAPEZOID { override fun toString(): String { return "⏢" } },
        RECTANGLE_LEFT { override fun toString(): String { return "LEFT ▭" } },
        RECTANGLE_CENTER { override fun toString(): String { return "CENTER ▭" } },
        RECTANGLE_RIGHT { override fun toString(): String { return "RIGHT ▭" } },
    }

    @FXML private lateinit var highInput: TextField
    @FXML private lateinit var lowInput: TextField
    @FXML private lateinit var funcChooser: ComboBox<MathFunction>
    @FXML private lateinit var epsInput: TextField
    @FXML private lateinit var methodChooser: ComboBox<SolveMethods>
    @FXML private lateinit var calcButton: Button
    @FXML private lateinit var outputField: TextArea

    /**
     * Инициализирует контроллер, настраивает слушателей событий.
     */
    fun initialize() {
        funcChooser.items = FXCollections.observableList(Main.functions)
        methodChooser.items = FXCollections.observableList(SolveMethods.values().toList())
        setOf(highInput, lowInput, epsInput).forEach {
            it.textProperty().addListener { _, _, newValue ->
                if (!newValue.matches(("\\d{0,4}([\\.]\\d{0,4})?").toRegex()))
                    outputField.let { textArea ->
                        textArea.appendText("Введите число типа Double длиною не больше 8 символов\n")
                        textArea.styleClass.add("error")
                    }
            }
        }
    }

    /**
     * Обрабатывает нажатия кнопки calcButton.
     */
    @FXML fun calcResult() {
        try {
            val a = lowInput.text.toDouble()
            val b = highInput.text.toDouble()
            val precision = epsInput.text.toDouble()
            val integral = Integral(funcChooser.value, Limits(Pair(a, b), precision))
            val answer = when (methodChooser.value) {
                SolveMethods.RECTANGLE_LEFT -> IntegralSolver.integrateByRectangle(integral, precision, RectangleMethodType.LEFT)
                SolveMethods.RECTANGLE_CENTER -> IntegralSolver.integrateByRectangle(integral, precision, RectangleMethodType.CENTER)
                SolveMethods.RECTANGLE_RIGHT -> IntegralSolver.integrateByRectangle(integral, precision, RectangleMethodType.RIGHT)
                SolveMethods.TRAPEZOID -> IntegralSolver.integrateByTrapezoid(integral, precision)
            }
            outputField.let {
                it.appendText("Значение интеграла = ${answer.resValue}\n" +
                        "Количество разбиений = ${answer.blocks}\n" +
                        "Погрешность = ${answer.infelicity}\n")
                it.styleClass.add("success")
            }
        } catch (e: Exception) {
            when (e) {
                is NumberFormatException -> outputField.appendText("Одно из полей заполнено неверно\n")
                is IllegalStateException -> outputField.appendText("Функция или метод не выбраны\n")
                else -> outputField.appendText(e.localizedMessage + "\n")
            }
            outputField.styleClass.add("error")
        }
    }
}
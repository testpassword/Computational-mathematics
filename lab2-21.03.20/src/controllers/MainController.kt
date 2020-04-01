package controllers

import javafx.beans.value.ChangeListener
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
 * @version 1.0
 */
class MainController {

    @FXML lateinit var highInput: TextField
    @FXML lateinit var lowInput: TextField
    @FXML lateinit var funcChooser: ComboBox<MathFunction>
    @FXML lateinit var epsInput: TextField
    @FXML lateinit var methodChooser: ComboBox<String>
    @FXML lateinit var calcButton: Button
    @FXML lateinit var outputField: TextArea

    /**
     * Инициализирует контроллер, настраивает слушателей событий.
     * @version 1.0
     */
    fun initialize() {
        funcChooser.items = FXCollections.observableList(Main.functions)
        funcChooser.setCellFactory { object: ListCell<MathFunction>() {
            override fun updateItem(item: MathFunction?, empty: Boolean) {
                super.updateItem(item, empty)
                text = if (empty) "null" else item!!.description
            }
        } }
        methodChooser.items = FXCollections.observableList(listOf("⏢", "LEFT ▭", "CENTER ▭", "RIGHT ▭"))
        val numericVerifier = ChangeListener<String> { _, _, newValue ->
            if (!newValue.matches(("\\d{0,4}([\\.]\\d{0,4})?").toRegex()))
                outputField.let {
                    it.appendText("Введите число типа Double длиною не больше 8 символов\n")
                    it.styleClass.add("error")
                }
        }
        lowInput.textProperty().addListener(numericVerifier)
        highInput.textProperty().addListener(numericVerifier)
        epsInput.textProperty().addListener(numericVerifier)
    }

    /**
     * Обрабатывает нажатия кнопки calcButton.
     * @version 1.0
     */
    @FXML fun calcResult() {
        try {
            val a = lowInput.text.toDouble()
            val b = highInput.text.toDouble()
            val precision = epsInput.text.toDouble()
            val func = funcChooser.value
            val integral = Integral(func, Limits(Pair(a, b), precision))
            val answer = when (methodChooser.value) {
                "LEFT ▭" -> IntegralSolver.integrateByRectangle(integral, precision, RectangleMethodType.LEFT)
                "CENTER ▭" -> IntegralSolver.integrateByRectangle(integral, precision, RectangleMethodType.CENTER)
                "RIGHT ▭" -> IntegralSolver.integrateByRectangle(integral, precision, RectangleMethodType.RIGHT)
                "⏢" -> IntegralSolver.integrateByTrapezoid(integral, precision)
                else -> throw Exception("Метод не определён.")
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
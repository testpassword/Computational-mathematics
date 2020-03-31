package controllers

import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import math.*
import java.lang.*
import math.IntegralSolver

class MainController {

    @FXML lateinit var highInput: TextField
    @FXML lateinit var lowInput: TextField
    @FXML lateinit var funcChooser: ComboBox<Int>
    @FXML lateinit var epsInput: TextField
    @FXML lateinit var methodChooser: ComboBox<String>
    @FXML lateinit var calcButton: Button
    @FXML lateinit var outputField: TextArea

    fun initialize() {
        funcChooser.items = FXCollections.observableList((0..4).toList())
        methodChooser.items = FXCollections.observableList(listOf("трапеция", "прямL", "прямC", "прямR"))
        val numericVerifier = ChangeListener<String> { _, _, newValue ->
            if (!newValue.matches(("\\d{0,4}([\\.]\\d{0,4})?").toRegex()))
                outputField.appendText("Введите число типа Double, длиною не больше восьми символов\n")
        }
        lowInput.textProperty().addListener(numericVerifier)
        highInput.textProperty().addListener(numericVerifier)
        epsInput.textProperty().addListener(numericVerifier)
    }

    @FXML fun calcResult() {
        try {
            val a = lowInput.text.toDouble()
            val b = highInput.text.toDouble()
            val precision = epsInput.text.toDouble()
            val funcNumber = funcChooser.value
            val integral = Integral(Main.functions[funcNumber], Limits(Pair(a, b), precision))
            val answer = when (methodChooser.value) {
                "трапеция" -> IntegralSolver.integrateByTrapezoid(integral, precision)
                "прямL" -> IntegralSolver.integrateByRectangle(integral, precision, IntegralSolver.RectangleMethodType.LEFT)
                "прямC" -> IntegralSolver.integrateByRectangle(integral, precision, IntegralSolver.RectangleMethodType.CENTER)
                "прямR" -> IntegralSolver.integrateByRectangle(integral, precision, IntegralSolver.RectangleMethodType.RIGHT)
                else -> throw Exception("никогда не вылетит")
            }
            outputField.appendText("""Значение интеграла = ${answer.resValue}
                    ~Количество разбиений = ${answer.blocks}
                    ~Погрешность = ${answer.infelicity}""".trimMargin("~"))
        } catch (e: Exception) {
            when (e) {
                is NumberFormatException -> outputField.appendText("Одно из полей заполнено неверно\n")
                is IllegalStateException -> outputField.appendText("Функция или метод не выбраны\n")
                else -> outputField.appendText(e.stackTrace.toString() + "\n")
            }
        }
    }
}
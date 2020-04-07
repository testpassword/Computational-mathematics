import controllers.launchConsole
import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import math.MathFunction
import kotlin.math.*

/**
 *Управляет загрузкой приложения.
 * @author Артемий Кульбако
 * @version 1.2
 */
class Main : Application() {

    /**
     * Отвечает за построение главной сцены JavaFX.
     * @param primaryStage сцена приложения.
     */
    override fun start(primaryStage: Stage?) {
        primaryStage?.let {
            it.scene = Scene(load(javaClass.getResource("/resources/main.fxml")))
            it.isResizable = false
            it.title = "Integrate Util"
            it.icons.add(Image("/resources/icon.png"))
            it.show()
        }
    }

    companion object {

        /**
         * Определяет метод взаимодействия с пользователем.
         * @param args - аргументы командной строки.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isNotEmpty() && args[0].equals("-c", true)) launchConsole()
            else launch(Main::class.java)
        }

        /**
         * Глобальное хранилище функций, доступных для интегрирования.
         */
        val functions = listOf(
            object: MathFunction {
                override fun func(xParam: Double) = xParam.pow(2)
                override fun toString() = "x^2"
            },
            object: MathFunction {
                override fun func(xParam: Double) = 1 / ln(xParam)
                override fun toString() = "1/ln(x)"
            },
            object: MathFunction {
                override fun func(xParam: Double) = cos(xParam) / (xParam + 2)
                override fun toString() = "cos(x)/(x+2)"
            },
            object: MathFunction {
                override fun func(xParam: Double) = sqrt(1 + 2 * xParam.pow(2) - xParam.pow(3))
                override fun toString() = "sqrt(1 + 2x^2 - x^3)"
            }
        )
    }
}
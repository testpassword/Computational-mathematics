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
     * @version 1.0
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
         * @version 1.1
         */
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isNotEmpty() && args[0].equals("-c", true)) launchConsole()
            else launch(Main::class.java)
        }

        /**
         * Хранилище функций, доступных для интегрирования.
         */
        val functions = mutableListOf(
            object: MathFunction {
                override fun func(xParam: Double) = xParam.pow(2)
                override var description = "x^2"
            },
            object: MathFunction {
                override fun func(xParam: Double) = 1 / ln(xParam)
                override var description = "1/ln(x)"
            },
            object: MathFunction {
                override fun func(xParam: Double) = cos(xParam) / (xParam + 2)
                override var description = "cos(x)/(x+2)"
            },
            object: MathFunction {
                override fun func(xParam: Double) = sqrt(1 + 2 * xParam.pow(2) - xParam.pow(3))
                override var description = "sqrt(1 + 2x^2 - x^3)"
            }
        )
    }
}
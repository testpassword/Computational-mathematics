import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Scene
import javafx.stage.Stage
import math.MathFunction
import kotlin.math.*

class Main : Application() {

    override fun start(primaryStage: Stage?) {
        primaryStage?.let {
            it.scene = Scene(load(javaClass.getResource("/resources/main.fxml")))
            it.isResizable = false
            it.title = "Integrate Util"
            it.show()
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isNotEmpty() && args[0].equals("-c", true)) launchConsole()
            else launch(Main::class.java)
        }

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
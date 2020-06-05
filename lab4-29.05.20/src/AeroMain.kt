import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.effect.BoxBlur
import javafx.scene.effect.Effect
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import java.awt.AWTException
import java.awt.Robot
import java.io.IOException

/**
 * Управляет взаимодействием с пользователем через графический интерфейс.
 * @author Артемий Кульбако.
 * @version 1.1
 */
class AeroMain: Application() {
    /**
     * Инициализирует главную сцену [stage], настраивает её эффекты.
     * @throws IOException при ошибке загрузки внешних ресурсов.
     */
    @Throws(IOException::class)
    override fun start(stage: Stage) {
        Companion.stage = stage
        layout.children.setAll(background, FXMLLoader.load(javaClass.getResource("/resources/main.fxml")))
        layout.style = "-fx-background-color: null"
        val scene = Scene(layout, Color.TRANSPARENT)
        Platform.setImplicitExit(false)
        stage.let {
            makeSmoke(it)
            it.title = "Approximation Util"
            it.initStyle(StageStyle.TRANSPARENT)
            it.scene = scene
            it.icons.add(Image("/resources/icons/window-w.png"))
            it.isResizable = false
            it.show()
            background.image = redrawBackground(it)
            background.effect = blur
            makeDraggable(it, layout)
        }
    }

    private fun redrawBackground(stage: Stage) =
        try {
            val image = Robot().createScreenCapture(
                java.awt.Rectangle(stage.x.toInt(), stage.y.toInt(), stage.width.toInt(), stage.height.toInt())
            )
            SwingFXUtils.toFXImage(image, null)
        } catch (e: AWTException) { null }

    private fun makeDraggable(stage: Stage, byNode: Node) {
        var x = 0.0
        var y = 0.0
        byNode.onMousePressed = EventHandler { mouseEvent: MouseEvent ->
            x = stage.x - mouseEvent.screenX
            y = stage.y - mouseEvent.screenY
        }
        var inDrag = false
        byNode.onMouseReleased = EventHandler { mouseEvent: MouseEvent? ->
            if (inDrag) {
                stage.hide()
                val pause = Timeline(KeyFrame(Duration.millis(5.0), EventHandler { event: ActionEvent? ->
                    background.image = redrawBackground(stage)
                    layout.children[0] = background
                    stage.show()
                }))
                pause.play()
            }
            inDrag = false
        }
        byNode.onMouseDragged = EventHandler { mouseEvent: MouseEvent ->
            stage.x = mouseEvent.screenX + x
            stage.y = mouseEvent.screenY + y
            layout.children[0] = makeSmoke(stage)
            inDrag = true
        }
    }

    private fun makeSmoke(stage: Stage) =
        Rectangle(stage.width, stage.height, Color.WHITESMOKE.deriveColor(0.0, 1.0, 1.0, 0.08))

    companion object {

        private val blur: Effect = BoxBlur(40.0, 40.0, 10)
        private val background = ImageView()
        private val layout = Pane()
        var stage: Stage? = null

        /** Точка запуска приложения, с аргументами командной строки [args].*/
        @JvmStatic
        fun main(args: Array<String>) = launch(AeroMain::class.java)
    }
}
package controllers

import java.awt.Desktop
import java.lang
import java.net.{URI, URL}
import java.util.ResourceBundle
import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.Cursor
import javafx.scene.control.{ComboBox, TextArea, TextField}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{BorderPane, HBox}
import math.MathFunction

/**
 * Управляет взаимодействием с пользователем посредством графического интерфейса.
 * @define RED_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @define GREEN_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @define BLUE_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @author Артемий Кульбако.
 * @version 1.1
 */
class GUIController extends Initializable {

  @FXML private var toolbar: HBox = _
  @FXML private var outputArea: TextArea = _
  @FXML private var mainPane: BorderPane = _
  @FXML private var gControl: GraphController = _
  //TODO: задать настоящий тип
  @FXML private var methodChooser: ComboBox[Any] = _
  @FXML private var funcChooser: ComboBox[MathFunction] = _
  @FXML private var leftBoundInput: TextField = _
  @FXML private var rightBoundInput: TextField = _
  val RED_LIGHT = new DropShadow(25.0, 0.0, 0.0, Color.RED)
  val BLUE_LIGHT = new DropShadow(25.0, 0.0, 0.0, Color.DEEPSKYBLUE)
  val GREEN_LIGHT = new DropShadow(25.0, 0.0, 0.0, Color.LIGHTGREEN)

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    toolbar.setOnMousePressed((_: MouseEvent) => { toolbar.setCursor(Cursor.MOVE) })
    toolbar.setOnMouseEntered((t: MouseEvent) => { if (!t.isPrimaryButtonDown) toolbar.setCursor(Cursor.HAND) })
    toolbar.setOnMouseExited((t: MouseEvent) => { if (!t.isPrimaryButtonDown) toolbar.setCursor(Cursor.DEFAULT) })
    Seq(methodChooser, funcChooser, leftBoundInput, rightBoundInput).foreach(it => {
      it.focusedProperty().addListener(new ChangeListener[lang.Boolean] {
        override def changed(obsVal: ObservableValue[_ <: lang.Boolean], oldVal: lang.Boolean, newVal: lang.Boolean): Unit = {
          it.setEffect(if (newVal) BLUE_LIGHT else null)
        }})
    })
    Seq(leftBoundInput, rightBoundInput).foreach(it => {
      it.textProperty().addListener(new ChangeListener[String] {
        override def changed(obsVal: ObservableValue[_ <: String], oldVal: String, newVal: String): Unit = {
          if (!newVal.matches("-?\\d{0,2}([.]\\d{0,6})?"))
            outputArea.appendText("Поля должны быть представлены числом, максимальная точность - 6 знаков после запятой")
        }})
    })
    val loader = new FXMLLoader(getClass.getResource("/resources/graph.fxml"))
    mainPane.setRight(loader.load())
    gControl = loader.getController.asInstanceOf[GraphController]
  }

  /** Минимизирует окно программы.*/
  @FXML def minimizeWindow(): Unit = { AeroMain.stage.setIconified(true) }

  /** Завершает работу программы.*/
  @FXML def closeProgram(): Unit = Platform.exit()

  /** Открывает профиль разработчика на github через стандартный веб-браузер в системе.*/
  @FXML def showCredits(): Unit = Desktop.getDesktop.browse(new URI("https://github.com/testpassword"))

  //TODO: printMessage с счётчиком строк

  @FXML private def showResult(): Unit = {
    println(
      """Один мальчик родился с гайкой вместо пупка. И он спросил у родителей, почему у него там гайка.
        |Родители пообещали ему рассказать об этом на его 14-летие. Ему исполнилось 14. И он опять подошёл и спросил
        |у родителей, почему у него вместо пупка гайка. Родители пообещали рассказать ему об этом когда ему будет 18 лет.
        |В 18 лет он спросил снова и родители рассказали ему, что есть один остров на котором растет пальма, а под этой
        |пальмой зарыт сундук. Парень долго копил денег и всё таки приехал на этот остров. Нашёл пальму, откопал сундук,
        |в котором лежала отвёртка. Он открутил гайку отвёрткой и у него отвалилась жопа.
        |""".stripMargin)
  }
}
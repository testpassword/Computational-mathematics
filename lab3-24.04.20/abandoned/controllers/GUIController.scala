package controllers

import java.awt.Desktop
import java.{lang, util}
import java.net.{URI, URL}
import java.util.ResourceBundle
import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.Cursor
import javafx.scene.control.{ComboBox, TableView, TextArea, TextField}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{HBox, VBox}
import math.{MathFunction, InterpolationService}

/**
 * Управляет взаимодействием с пользователем посредством графического интерфейса.
 * @define RED_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @define GREEN_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @define BLUE_LIGHT эффект красного свечения по контуру объекта; может быть применён к объекту {@see Node}.
 * @author Артемий Кульбако.
 * @version 1.2
 */
class GUIController extends Initializable {

  //TODO: задать настоящие типы
  @FXML private var toolbar: HBox = _
  @FXML private var gControl: GraphController = _
  @FXML private var methodChooser: ComboBox[InterpolationService.SolveMethods] = _
  @FXML private var funcChooser: ComboBox[MathFunction] = _
  @FXML private var leftBoundInput: TextField = _
  @FXML private var rightBoundInput: TextField = _
  @FXML private var outputArea: TextArea = _
  @FXML private var pointsAmountFld: TextField = _
  @FXML private var pointsTable: TableView[Any] = _
  @FXML private var graphPlaceholder: VBox = _
  private val fxMethods = FXCollections.observableArrayList(InterpolationService.SolveMethods.NEWTON_POLYNOMIAL)
  private val fxEqs = FXCollections.observableArrayList(InterpolationService.equations)
  val RED_LIGHT = new DropShadow(25.0, 0.0, 0.0, Color.RED)
  val BLUE_LIGHT = new DropShadow(25.0, 0.0, 0.0, Color.DEEPSKYBLUE)
  val GREEN_LIGHT = new DropShadow(25.0, 0.0, 0.0, Color.LIGHTGREEN)
  private object Counter { var lineNumber = 0 }

  /**
   * Инициализирует экземпляр класс GUIController, навешивая на различные его элементы обработчики событий и эффекты
   * постобработки.
   * @param url адрес внешнего ресурса, который может быть загружен.
   * @param resourceBundle загруженный внешний ресурс, который может быть использован.
   */
  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    toolbar.setOnMousePressed((_: MouseEvent) => { toolbar.setCursor(Cursor.MOVE) })
    toolbar.setOnMouseEntered((t: MouseEvent) => { if (!t.isPrimaryButtonDown) toolbar.setCursor(Cursor.HAND) })
    toolbar.setOnMouseExited((t: MouseEvent) => { if (!t.isPrimaryButtonDown) toolbar.setCursor(Cursor.DEFAULT) })
    Seq(methodChooser, funcChooser, leftBoundInput, rightBoundInput, pointsAmountFld, pointsTable).foreach(it => {
      it.focusedProperty().addListener(new ChangeListener[lang.Boolean] {
        override def changed(obsVal: ObservableValue[_ <: lang.Boolean], oldVal: lang.Boolean, newVal: lang.Boolean): Unit = {
          it.setEffect(if (newVal) BLUE_LIGHT else null)
        }})
    })
    Seq(leftBoundInput, rightBoundInput).foreach(it => {
      it.textProperty().addListener(new ChangeListener[String] {
        override def changed(obsVal: ObservableValue[_ <: String], oldVal: String, newVal: String): Unit = {
          if (!newVal.matches("-?\\d{0,2}([.]\\d{0,6})?"))
            printMessage(RED_LIGHT, "Поля должны быть представлены числом", "Максимальная точность - 6 знаков после запятой")
        }})
    })
    pointsAmountFld.textProperty().addListener(new ChangeListener[String] {
      override def changed(obsVal: ObservableValue[_ <: String], oldVal: String, newVal: String): Unit = {
        if (!newVal.matches("^$|([1-9]|1[013])$"))
          printMessage(RED_LIGHT, "Поле должно быть целым числом, от 1 до 13")
      }
    })
    methodChooser.setItems(fxMethods)
    funcChooser.setItems(fxEqs)
    val loader = new FXMLLoader(getClass.getResource("/resources/graph.fxml"))
    graphPlaceholder.getChildren.add(loader.load())
    gControl = loader.getController.asInstanceOf[GraphController]
  }

  /** Минимизирует окно программы.*/
  @FXML def minimizeWindow(): Unit = { AeroMain.stage.setIconified(true) }

  /** Завершает работу программы.*/
  @FXML def closeProgram(): Unit = Platform.exit()

  /** Открывает профиль разработчика на github через установленный по-умолчанию веб-браузер в системе.*/
  @FXML def showCredits(): Unit = Desktop.getDesktop.browse(new URI("https://github.com/testpassword"))

  /** Печатает сообщение в главное окно вывода. */
  @FXML def printMessage(lightning: DropShadow, strings: String*): Unit = {
    strings.foreach(it => {
      outputArea.appendText(s"${Counter.lineNumber}. $it\n")
      Counter.lineNumber += 1
    })
    outputArea.setEffect(lightning)
  }

  @FXML private def showResult(): Unit = {
    try {
      val method = methodChooser.getValue
      val f = funcChooser.getValue
      val borders = (leftBoundInput.getText.toDouble, rightBoundInput.getText.toDouble)
      val n = pointsAmountFld.getText.toInt
      val res = InterpolationService.solve()
      gControl.clear()
      gControl.drawLine(f)
      /*
      Нарисовать точки
      Заполнить таблицу x и y, количество строк = n

      Пользователь может подправить точки в таблице, и сгенерировать график заново
      После генер. доступа вычисление в любой
      После подправления заблокировать выч. в любой точке
       */
    } catch {
      case _: NumberFormatException => printMessage(RED_LIGHT, "Одно из полей не заполнено или заполнено неверно",
        "Читай подсказки при вводе данных")
      case e: Throwable => printMessage(RED_LIGHT, e.getLocalizedMessage)
    }
  }
}
package controllers

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.control.TableView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.stage.Stage
import java.awt.Desktop
import java.io.IOException
import java.net.URI


class GUIController {

    @FXML lateinit var toolbar: HBox
    @FXML lateinit var minimizeBtn: Button
    //TODO: изменить тип таблицы
    @FXML lateinit var contentTable: TableView<String>

    fun initialize() {
        minimizeBtn.onAction = EventHandler { event -> ((event!!.source as Button).scene.window as Stage).isIconified = true }
        //TODO: перемещение за другой node
        toolbar.let {
            it.onMousePressed = EventHandler { mouseEvent: MouseEvent -> it.cursor = Cursor.MOVE }
            it.onMouseEntered = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { it.cursor = Cursor.HAND } }
            it.onMouseExited = EventHandler { mouseEvent: MouseEvent -> if (!mouseEvent.isPrimaryButtonDown) { toolbar.cursor = Cursor.DEFAULT } }
        }
    }

    @FXML fun closeProgram() = Platform.exit()

    @FXML fun showCredits() {
        try {
            Desktop.getDesktop().browse(URI("https://github.com/testpassword"))
        } catch (e: IOException) { e.printStackTrace() }
    }

    @FXML fun clearTable() = contentTable.items.clear()
}
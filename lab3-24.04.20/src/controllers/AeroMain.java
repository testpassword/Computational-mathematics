package controllers;

import javafx.animation.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import javafx.util.Duration;
import java.awt.*;
import java.io.IOException;

/**
 * Управляет взаимодействием с пользователем через графический интерфейс.
 * @author Артемий Кульбако.
 * @version 1.1
 */
public class AeroMain extends Application {

    private static final Effect blur = new BoxBlur(40, 40, 10);
    private static final ImageView background = new ImageView();
    private static final Pane layout = new Pane();
    public static Stage stage;

    /**
     * Инициализирует главную сцену, настраивает её эффекты.
     * @param stage сцена
     * @throws IOException при ошибке загрузки внешних ресурсов.
     */
    @Override public void start(Stage stage) throws IOException {
        AeroMain.stage = stage;
        layout.getChildren().setAll(background, FXMLLoader.load(getClass().getResource("/resources/main.fxml")));
        layout.setStyle("-fx-background-color: null");
        Scene scene = new Scene(layout, Color.TRANSPARENT);
        stage.setTitle("Approximation Util");
        Platform.setImplicitExit(false);
        makeSmoke(stage);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/resources/icons/window-w.png"));
        stage.setResizable(false);
        stage.show();
        background.setImage(redrawBackground(stage));
        background.setEffect(blur);
        makeDraggable(stage, layout);
    }

    private Image redrawBackground(Stage stage) {
        try {
            var robot = new Robot();
            var image = robot.createScreenCapture(new java.awt.Rectangle(
                    (int) stage.getX(), (int) stage.getY(), (int) stage.getWidth(), (int) stage.getHeight()));
            return SwingFXUtils.toFXImage(image, null);
        } catch (java.awt.AWTException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class Delta {
        double x, y;
    }

    private void makeDraggable(Stage stage, Node byNode) {
        final var dragDelta = new Delta();
        byNode.setOnMousePressed(mouseEvent -> {
            dragDelta.x = stage.getX() - mouseEvent.getScreenX();
            dragDelta.y = stage.getY() - mouseEvent.getScreenY();
        });
        final var inDrag = new SimpleBooleanProperty(false);
        byNode.setOnMouseReleased(mouseEvent -> {
            if (inDrag.get()) {
                stage.hide();
                Timeline pause = new Timeline(new KeyFrame(Duration.millis(5), event -> {
                    background.setImage(redrawBackground(stage));
                    layout.getChildren().set(0, background);
                    stage.show();
                }));
                pause.play();
            }
            inDrag.set(false);
        });
        byNode.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);
            layout.getChildren().set(0, makeSmoke(stage));
            inDrag.set(true);
        });
    }

    private Rectangle makeSmoke(Stage stage) {
        return new javafx.scene.shape.Rectangle(stage.getWidth(), stage.getHeight(),
                Color.WHITESMOKE.deriveColor(0, 1, 1, 0.08));
    }

    /**
     * Точка запуска приложения.
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) { launch(args); }
}
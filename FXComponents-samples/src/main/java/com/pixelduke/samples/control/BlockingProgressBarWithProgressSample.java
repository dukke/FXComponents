package com.pixelduke.samples.control;

import com.pixelduke.control.BlockingProgressBar;
import com.pixelduke.control.FlatAlert;
import com.pixelduke.transit.Style;
import com.pixelduke.transit.TransitTheme;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BlockingProgressBarWithProgressSample extends Application {
    private String errorMessage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        System.setProperty("prism.lcdtext", "false");

        StackPane stackPane = new StackPane();
        Button button = new Button("Show BlockingProgressBar");
        stackPane.getChildren().add(button);
        button.setOnAction(event -> {
            BlockingProgressBar blockingProgressBar = new BlockingProgressBar("Processing results. Please wait...");
            blockingProgressBar.initOwner(stage);
            blockingProgressBar.setOnHidden(dialogEvent -> {
                if (errorMessage != null) {
                    ButtonType cancelButton = ButtonType.CANCEL;
                    FlatAlert flatAlert = new FlatAlert(Alert.AlertType.ERROR,
                            "Unable to complete operation.\nThe Selection program failed to launch.\nWindows Error code " + errorMessage + "\nClose other applications and try again",
                            cancelButton);
                    flatAlert.initOwner(stage);
                    flatAlert.showAndWait();
                }
            });
            blockingProgressBar.showAndWait((new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    int totalIterations = 10;

                    updateProgress(0, totalIterations);
                    for (int i = 0 ; i < totalIterations; ++i) {
                        Thread.sleep(500);
                        updateProgress(i, totalIterations);
                        updateMessage("Finished processing " + (i + 1) + " out of " + totalIterations + " tags.");
                    }
                    return null;
                }
            }));

        });

        Scene scene = new Scene(stackPane, 800, 600);
        stage.setTitle("Button Sample");

        new TransitTheme(scene, Style.LIGHT);

        stage.setScene(scene);
        stage.show();
    }
}



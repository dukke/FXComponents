package com.pixelduke.samples.control;

import com.pixelduke.control.BlockingProgressBar;
import com.pixelduke.control.FlatAlert;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BlockingProgressBarSample extends Application {


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
                protected Void call() {
                    try {
                        ProcessBuilder pb = new ProcessBuilder("notepad.exe");
                        pb.redirectErrorStream(true);
                        Process process = pb.start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null)
                            System.out.println("tasklist: " + line);
                        process.waitFor();
                    } catch (InterruptedException | IOException e) {
                        errorMessage = e.getMessage();
                    }

                    return null;
                }
            }));

        });

        Scene scene = new Scene(stackPane, 800, 600);
        stage.setTitle("Button Sample");

        new JMetro(scene, Style.LIGHT);

        stage.setScene(scene);
        stage.show();
    }
}



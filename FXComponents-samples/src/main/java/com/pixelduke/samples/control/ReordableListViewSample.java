package com.pixelduke.samples.control;

import com.pixelduke.control.ReordableListView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import java.util.ArrayList;
import java.util.List;

public class ReordableListViewSample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        System.setProperty("prism.lcdtext", "false");

        ObservableList<String> sourceList = FXCollections.observableList(new ArrayList<>(List.of("Apples", "Oranges", "Bananas", "Lemons",
                "Grapes")));

        Label heading = new Label("Reordable ListView");
        heading.setStyle("-fx-font-family: 'Segoe UI Light'; -fx-font-size: 2em;");

        ReordableListView<String> listView = new ReordableListView<>();
        listView.setItems(sourceList);

        VBox vBox = new VBox(heading, listView);
        vBox.setSpacing(25);

        StackPane root = new StackPane(vBox);
        root.setPadding(new Insets(20, 50, 50, 50));

        Scene scene = new Scene(root);
        stage.setTitle("ReordableListView Sample");

        new JMetro(scene, Style.LIGHT);

        stage.setScene(scene);
        stage.show();
    }
}

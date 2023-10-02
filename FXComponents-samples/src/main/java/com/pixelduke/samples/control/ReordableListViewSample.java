package com.pixelduke.samples.control;

import com.pixelduke.control.ReordableListView;
import com.pixelduke.transit.Style;
import com.pixelduke.transit.TransitTheme;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

//        Label heading = new Label("Reordable ListView");
//        heading.setStyle("-fx-font-family: 'Segoe UI Light'; -fx-font-size: 2em;");

        ReordableListView<String> listView = new ReordableListView<>();
        listView.setItems(sourceList);

//        VBox vBox = new VBox(heading, listView);
        VBox vBox = new VBox(listView);
        vBox.setSpacing(25);

        StackPane root = new StackPane(vBox);
        root.setPadding(new Insets(20, 20, 50, 20));

        Scene scene = new Scene(root);
        stage.setTitle("ReordableListView Sample");

        new TransitTheme(scene, Style.LIGHT);

        stage.setScene(scene);
        stage.show();
    }
}

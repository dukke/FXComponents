package com.pixelduke.control;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
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

        ObservableList<String> sourceList = FXCollections.observableList(new ArrayList<>(List.of("X1", "X2", "X3", "X4",
                "X5")));

        ReordableListView<String> listView = new ReordableListView<>();
        listView.setItems(sourceList);

        Scene scene = new Scene(listView);
        stage.setTitle("ReordableListView Sample");

        new JMetro(scene, Style.LIGHT);

        stage.setScene(scene);
        stage.show();
    }
}

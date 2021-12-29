package com.pixelduke.control;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.ArrayList;
import java.util.List;

public class ListBuilderSample extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        System.setProperty("prism.lcdtext", "false");


        ObservableList<String> sourceList = FXCollections.observableList(new ArrayList<>(List.of("Separator",
                "Read Mail", "Size", "Encoding", "Edit")));

        ListBuilder<String> listBuilder = new ListBuilder<>("TEST");
        listBuilder.setSourceItems(sourceList);

        listBuilder.setSourceHeader(new Label("Available Items:"));
        listBuilder.setTargetHeader(new Label("To Print:"));

        Scene scene = new Scene(listBuilder);
        stage.setTitle("Button Sample");

        new JMetro(scene, Style.LIGHT);

        stage.setScene(scene);
        stage.show();
    }
}
package com.pixelduke.samples.control;

import com.pixelduke.control.ListBuilder;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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

        VBox vBox = new VBox(listBuilder);
        vBox.getStyleClass().add("container");
        Scene scene = new Scene(vBox);
        stage.setTitle("ListBuilder Sample");

        JMetro jMetro = new JMetro(scene, Style.LIGHT);
        jMetro.getOverridingStylesheets().add(ListBuilderSample.class.getResource("list-builder-sample.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }
}
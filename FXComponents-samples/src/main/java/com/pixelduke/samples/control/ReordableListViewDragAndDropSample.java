package com.pixelduke.samples.control;

import com.pixelduke.control.ReordableListView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ReordableListViewDragAndDropSample extends Application {

    private TreeItem<String> rootNode = new TreeItem<>("Root");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        HBox mainContainer = new HBox();

        System.setProperty("prism.lcdtext", "false");

        rootNode.setExpanded(true);

        ObservableList<String> listViewList = FXCollections.observableList(new ArrayList<>(List.of("X1", "X2", "X3", "X4",
                "X5")));

        ReordableListView<String> listView = new ReordableListView<>();
        TreeView<String> treeView = new TreeView<>(rootNode);

        listView.setCellFactory(param -> new CustomListViewCell());

        treeView.setCellFactory(param -> new CustomTreeCell());

        rootNode.getChildren().addAll(new TreeItem<>("X6"),
                                      new TreeItem<>("X7"),
                                      new TreeItem<>("X8"));

        listView.setItems(listViewList);

        mainContainer.getChildren().addAll(listView, treeView);
//        mainContainer.getChildren().add(listView);

        Scene scene = new Scene(mainContainer);
        stage.setTitle("ReordableListView Sample");

//        new JMetro(scene, Style.LIGHT);

        stage.setScene(scene);
        stage.show();
    }

    private static class CustomListViewCell extends ReordableListView.DraggableCell<String> {
        private static final String tempPlaceholderItem = "ADDED FROM OUTSIDE SOURCE";
        private static boolean hasAddedTempItem = false;

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null || isDropTargetCell || item == tempPlaceholderItem) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item);
                setGraphic(null);
            }
        }

        @Override
        protected void onDragExitedFromOutsideSource(DragEvent dragEvent) {
            if (hasAddedTempItem) {
                hasAddedTempItem = false;
                getListView().getItems().remove(tempPlaceholderItem);
            }
        }

        @Override
        protected boolean onDragDroppedFromOutsideSource(DragEvent dragEvent) {
            ListView<String> listView = getListView();
            Dragboard dragBoard = dragEvent.getDragboard();

            String text = dragBoard.getString();
            int indexOfPlaceholder = listView.getItems().indexOf(tempPlaceholderItem);

            listView.getItems().set(indexOfPlaceholder, text);

            hasAddedTempItem = false;

            return true;
        }

        @Override
        protected String getPlaceholderItem() {
            return tempPlaceholderItem;
        }
    }

    private static class CustomTreeCell extends TreeCell<String> {

        public CustomTreeCell() {
            setOnDragDetected(event -> {
                System.out.println("CUSTOM TREE CELL -DRAG DETECTED");
                if (getItem() == null) {
                    return;
                }

                Dragboard dragboard = startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putString(getItem());

                Image dragImage = this.snapshot(new SnapshotParameters(), null);

                dragboard.setDragView(dragImage);
                dragboard.setContent(content);

                event.consume();
            });

            setOnDragDone(DragEvent::consume);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item);
            }
        }
    }
}


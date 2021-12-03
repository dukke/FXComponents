package com.pixelduke.control;

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

        private static int previousDropTargetIndex = -1;
        private static CustomListViewCell previousDropTargetCell;

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null || isDropTargetCell || item.equals(tempPlaceholderItem)) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item);
                setGraphic(null);
            }
        }

        @Override
        protected void onDragEnteredFromOutsideSource(DragEvent dragEvent) {
            ListView<String> listView = getListView();

            String currItem = getItem();
            int indexOfCurrItem = listView.getItems().indexOf(currItem);

            if (!hasAddedTempItem) {
                listView.getItems().add(indexOfCurrItem, tempPlaceholderItem);
                hasAddedTempItem = true;
            }

            if (isEmpty()) {
                return;
            }

            setIsDropTargetCell(true);
            if (previousDropTargetCell != null) {
                previousDropTargetCell.setIsDropTargetCell(false);

                if (indexOfCurrItem > previousDropTargetIndex) {
                    listView.getItems().set(indexOfCurrItem - 1, currItem);
                    listView.getItems().set(indexOfCurrItem, tempPlaceholderItem);
                } else if (indexOfCurrItem < previousDropTargetIndex){
                    listView.getItems().set(indexOfCurrItem + 1, currItem);
                    listView.getItems().set(indexOfCurrItem, tempPlaceholderItem);
                }
            } else {
                // First time entering this Node from outside
                if (indexOfCurrItem < listView.getItems().size() - 1) {
                    listView.getItems().add(indexOfCurrItem + 1, currItem);
                    listView.getItems().remove(tempPlaceholderItem);
                    listView.getItems().set(indexOfCurrItem, tempPlaceholderItem);
                }
            }
            previousDropTargetCell = this;
            previousDropTargetIndex = indexOfCurrItem;

            updateItem(currItem, isEmpty());
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
            previousDropTargetCell = null;

            return true;
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


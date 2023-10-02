package impl.com.pixelduke.control;

import com.pixelduke.control.ListBuilder;
import com.pixelduke.control.ReordableListView;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javafx.scene.control.SelectionMode.MULTIPLE;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

public class ListBuilderSkin<T> extends SkinBase<ListBuilder<T>> {
    public static final DataFormat LIST_BUILDER_DATA_FORMAT = new DataFormat("impl.java.pixelduke.control.ListBuilderSkin");

    private static Object draggedItem;

    private final GridPane gridPane = new GridPane();

    private final ReordableListView<T> sourceListView = new ReordableListView<>();
    private final ReordableListView<T> targetListView = new ReordableListView<>();

    private final Button addButton = new Button("Add");
    private final Button removeButton = new Button("Remove");
    private final Button addAllButton = new Button("Add All");
    private final Button removeAllButton = new Button("Remove All");

    private final Button moveUpButton = new Button("Move Up");
    private final Button moveDownButton = new Button("Move Down");

    /*=========================================================================*
     *                                                                         *
     *                        CONSTRUCTORS                                     *
     *                                                                         *
     *=========================================================================*/

    public ListBuilderSkin(ListBuilder<T> listBuilder) {
        super(listBuilder);

        initGridPane();
        initListViews();
        initButtons();

        getChildren().add(gridPane);

        updateView();

        // Setup double click on lists
        sourceListView.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                moveToTargetList();
            }
        });

        targetListView.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                moveToSourceList();
            }
        });

        // Setup dragging and dropping from source list view into target list view when target list view is empty
        targetListView.setOnDragOver(event -> {
            if (targetListView.getItems().size() != 0) {
                return;
            }

            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasContent(LIST_BUILDER_DATA_FORMAT)) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });

        targetListView.setOnDragDropped(event -> {
            if (targetListView.getItems().size() != 0) {
                return;
            }

            Dragboard dragBoard = event.getDragboard();
            boolean success = false;

            if (dragBoard.hasContent(LIST_BUILDER_DATA_FORMAT)) {
                move(sourceListView, targetListView, List.of(draggedItem));
                success = true;
            }
            event.setDropCompleted(success);

            event.consume();

            clearAndSelectElements(targetListView, (List<T>) List.of(draggedItem));
            sourceListView.getSelectionModel().clearSelection();
        });

        // Setup dragging and dropping from target list view into source list view when source list view is empty
        sourceListView.setOnDragOver(event -> {
            if (sourceListView.getItems().size() != 0) {
                return;
            }

            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasContent(LIST_BUILDER_DATA_FORMAT)) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });

        sourceListView.setOnDragDropped(event -> {
            if (sourceListView.getItems().size() != 0) {
                return;
            }

            Dragboard dragBoard = event.getDragboard();
            boolean success = false;

            if (dragBoard.hasContent(LIST_BUILDER_DATA_FORMAT)) {
                move(targetListView, sourceListView, List.of(draggedItem));
                success = true;
            }
            event.setDropCompleted(success);

            event.consume();

            clearAndSelectElements(sourceListView, (List<T>) List.of(draggedItem));
            targetListView.getSelectionModel().clearSelection();
        });

        sourceListView.getSelectionModel().selectedItemProperty().addListener(observable -> updateAddEnabledState());
        sourceListView.getItems().addListener((ListChangeListener<? super T>) changed -> updateAddAllEnabledState());
        targetListView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            updateRemoveEnabledState();
            updateMoveUpEnabledState();
            updateMoveDownEnabledState();
        });
        targetListView.getItems().addListener((ListChangeListener<? super T>) changed -> updateRemoveAllEnabledState());

        updateAddEnabledState();
        updateRemoveEnabledState();
        updateAddAllEnabledState();
        updateRemoveAllEnabledState();

        updateMoveUpEnabledState();
        updateMoveDownEnabledState();

        sourceListView.getSelectionModel().selectAll();

        // CSS
        gridPane.getStyleClass().add("main-container");

    }

    /*=========================================================================*
     *                                                                         *
     *                        PRIVATE API                                      *
     *                                                                         *
     *=========================================================================*/

    private void initButtons() {
        addButton.setContentDisplay(ContentDisplay.RIGHT);
        addAllButton.setContentDisplay(ContentDisplay.RIGHT);

        addButton.setOnAction(actionEvent -> move(sourceListView, targetListView));
        removeButton.setOnAction(actionEvent -> move(targetListView, sourceListView));
        moveUpButton.setOnAction(actionEvent -> moveSelectedElementsUp(targetListView));
        moveDownButton.setOnAction(actionEvent -> moveSelectedElementsDown(targetListView));

        addAllButton.setOnAction(actionEvent -> move(sourceListView, targetListView, List.copyOf(sourceListView.getItems())));
        removeAllButton.setOnAction(actionEvent -> move(targetListView, sourceListView, List.copyOf(targetListView.getItems())));
    }

    private void initListViews() {
        sourceListView.getStyleClass().add("source-list-view");
        targetListView.getStyleClass().add("target-list-view");

        sourceListView.setCellFactory(param -> new ListBuilderCell<>(getSkinnable()));
        targetListView.setCellFactory(param -> new ListBuilderCell<>(getSkinnable()));

        Bindings.bindContentBidirectional(sourceListView.getItems(), getSkinnable().getSourceItems());
        Bindings.bindContentBidirectional(targetListView.getItems(), getSkinnable().getTargetItems());

        sourceListView.getSelectionModel().setSelectionMode(MULTIPLE);
        targetListView.getSelectionModel().setSelectionMode(MULTIPLE);
    }

    private void initGridPane() {
        gridPane.getStyleClass().add("grid-pane");
        setViewConstraints();
    }

    private void moveSelectedElementsUp(ListView<T> listView) {
        List<T> selectedElements = List.copyOf(listView.getSelectionModel().getSelectedItems());

        for (T element : selectedElements) {
            int index = listView.getItems().indexOf(element);
            if (index != 0) {
                listView.getItems().remove(element);
                listView.getItems().add(index - 1, element);
            }
        }

        clearAndSelectElements(listView, selectedElements);
    }

    private void moveSelectedElementsDown(ListView<T> listView) {
        List<T> selectedElements = List.copyOf(listView.getSelectionModel().getSelectedItems());

        for (int index = selectedElements.size() - 1; index >= 0; index--) {
            T element = selectedElements.get(index);

            int elementIndexOnList = listView.getItems().indexOf(element);
            if (elementIndexOnList < listView.getItems().size() - 1) {
                listView.getItems().remove(element);
                listView.getItems().add(elementIndexOnList + 1, element);
            }
        }

        clearAndSelectElements(listView, selectedElements);
    }

    private void setViewConstraints() {
        ColumnConstraints col1 = new ColumnConstraints();

        col1.setFillWidth(true);
        col1.setHgrow(Priority.ALWAYS);
        col1.setMaxWidth(Double.MAX_VALUE);
        col1.setPrefWidth(200);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setFillWidth(true);
        col2.setHgrow(Priority.NEVER);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setFillWidth(true);
        col3.setHgrow(Priority.ALWAYS);
        col3.setMaxWidth(Double.MAX_VALUE);
        col3.setPrefWidth(200);

        gridPane.getColumnConstraints().setAll(col1, col2, col3);

        RowConstraints row1 = new RowConstraints();
        row1.setFillHeight(true);
        row1.setVgrow(Priority.NEVER);

        RowConstraints row2 = new RowConstraints();
        row2.setMaxHeight(Double.MAX_VALUE);
        row2.setPrefHeight(200);
        row2.setVgrow(Priority.ALWAYS);

        RowConstraints row3 = new RowConstraints();
        row3.setFillHeight(true);
        row3.setVgrow(Priority.NEVER);

        gridPane.setHgap(5);

        gridPane.getRowConstraints().setAll(row1, row2, row3);
    }

    private VBox createListElementsMoveControls() {
        VBox box = new VBox(5);
        box.setFillWidth(true);
        box.setAlignment(Pos.CENTER);

        StackPane addButtonContainer = new StackPane(addButton);
        StackPane addAllButtonContainer = new StackPane(addAllButton);
        StackPane removeButtonContainer = new StackPane(removeButton);
        StackPane removeAllButtonContainer = new StackPane(removeAllButton);

        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setGraphic(new ImageView(Objects.requireNonNull(ListBuilder.class.getResource("more-than-16.png")).toExternalForm()));
        addAllButton.setMaxWidth(Double.MAX_VALUE);
        addAllButton.setGraphic(new ImageView(Objects.requireNonNull(ListBuilder.class.getResource("double-right-16.png")).toExternalForm()));
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setGraphic(new ImageView(Objects.requireNonNull(ListBuilder.class.getResource("less-than-16.png")).toExternalForm()));
        removeAllButton.setMaxWidth(Double.MAX_VALUE);
        removeAllButton.setGraphic(new ImageView(Objects.requireNonNull(ListBuilder.class.getResource("double-left-16.png")).toExternalForm()));

        box.getChildren().addAll(addButtonContainer, addAllButtonContainer, removeButtonContainer, removeAllButtonContainer);

        // CSS
        addButtonContainer.getStyleClass().add("add-button-container");
        addAllButtonContainer.getStyleClass().add("add-all-button-container");
        removeButtonContainer.getStyleClass().add("remove-button-container");
        removeAllButtonContainer.getStyleClass().add("remove-all-button-container");

        return box;
    }

    private Pane createTargetListControls() {
        moveUpButton.setMaxWidth(Double.MAX_VALUE);
        moveUpButton.setGraphic(new ImageView(Objects.requireNonNull(ListBuilder.class.getResource("up-16.png")).toExternalForm()));
        moveDownButton.setMaxWidth(Double.MAX_VALUE);
        moveDownButton.setGraphic(new ImageView(Objects.requireNonNull(ListBuilder.class.getResource("down-16.png")).toExternalForm()));

        VBox box = new VBox(5);
        box.setFillWidth(true);
        box.setAlignment(Pos.CENTER);

        box.getChildren().addAll(moveUpButton, moveDownButton);

        return box;
    }

    private void updateView() {
        gridPane.getChildren().clear();

        Node sourceHeader = getSkinnable().getSourceHeader();
        Node targetHeader = getSkinnable().getTargetHeader();

        VBox sourceHeaderContainer = new VBox(sourceHeader);
        VBox targetHeaderContainer = new VBox(targetHeader);

        setViewConstraints();

        if (sourceHeader != null) {
            gridPane.add(sourceHeaderContainer, 0, 0);
        }

        if (targetHeader != null) {
            gridPane.add(targetHeaderContainer, 2, 0);
        }

        gridPane.add(sourceListView, 0, 1);
        gridPane.add(targetListView, 2, 1);

        StackPane elementsMoveTargetListViewPane = new StackPane();
        elementsMoveTargetListViewPane.setAlignment(Pos.CENTER);
        elementsMoveTargetListViewPane.getChildren().add(createListElementsMoveControls());
        gridPane.add(elementsMoveTargetListViewPane, 1, 1);

        StackPane targetListControlsPane = new StackPane();
        targetListControlsPane.setAlignment(Pos.CENTER);
        targetListControlsPane.getChildren().add(createTargetListControls());
        gridPane.add(targetListControlsPane, 3, 1);

        // CSS
        sourceHeaderContainer.getStyleClass().add("source-header-container");
        targetHeaderContainer.getStyleClass().add("target-header-container");
    }

    private void moveToTargetList() {
        move(sourceListView, targetListView);
        sourceListView.getSelectionModel().clearSelection();
    }

    private void moveToSourceList() {
        move(targetListView, sourceListView);
        targetListView.getSelectionModel().clearSelection();
    }

    private static void move(ListView<?> listViewA, ListView<?> listViewB) {
        List<?> selectedItems = new ArrayList<>(listViewA.getSelectionModel().getSelectedItems());
        move(listViewA, listViewB, selectedItems);
    }

    private static void move(ListView<?> viewA, ListView<?> viewB, List items) {
        viewA.getItems().removeAll(items);
        viewB.getItems().addAll(items);
    }

    private static void move(ListView<?> viewA, ListView<?> viewB, List items, int destinationIndex) {
        viewA.getItems().removeAll(items);
        viewB.getItems().addAll(destinationIndex, items);
    }

    private static <E> void clearAndSelectElements(ListView<E> listView, List<E> elementsToSelect) {
        listView.getSelectionModel().clearSelection();
        for (E element : elementsToSelect) {
            int index = listView.getItems().indexOf(element);
            listView.getSelectionModel().select(index);
        }
    }

    private void updateAddEnabledState() {
        boolean areSourceListItemsSelected = sourceListView.getSelectionModel().getSelectedItems().size() > 0;
        addButton.setDisable(!areSourceListItemsSelected);
    }

    private void updateRemoveEnabledState() {
        boolean areTargetListItemsSelected = targetListView.getSelectionModel().getSelectedItems().size() > 0;
        removeButton.setDisable(!areTargetListItemsSelected);
    }

    private void updateAddAllEnabledState() {
        boolean isSourceListEmpty = sourceListView.getItems().size() == 0;
        addAllButton.setDisable(isSourceListEmpty);
    }

    private void updateRemoveAllEnabledState() {
        boolean isTargetListEmpty = targetListView.getItems().size() == 0;
        removeAllButton.setDisable(isTargetListEmpty);
    }

    private void updateMoveUpEnabledState() {
        boolean isTargetListItemSelected = targetListView.getSelectionModel().getSelectedItems().size() > 0;
        moveUpButton.setDisable(!isTargetListItemSelected);
    }

    private void updateMoveDownEnabledState() {
        boolean isTargetListItemSelected = targetListView.getSelectionModel().getSelectedItems().size() > 0;
        moveDownButton.setDisable(!isTargetListItemSelected);
    }

    /*=========================================================================*
     *                                                                         *
     *                      SUPPORTING CLASSES                                 *
     *                                                                         *
     *=========================================================================*/

     private static class ListBuilderCell<E> extends ReordableListView.DraggableCell<E> {
         private final ListBuilder<E> listBuilder;

         public ListBuilderCell(ListBuilder<E> listBuilder) {
             this.listBuilder = listBuilder;
         }

         @Override
         protected void updateItem(E item, boolean empty) {
             super.updateItem(item, empty);

             if (empty || item == null || isDropTargetCell || item == getPlaceholderItem()) {
                 setText(null);
                 setGraphic(null);
             } else {
                 setText(item.toString());
                 setGraphic(null);
             }
         }

         @Override
         protected void onDragExitedFromOutsideSource(DragEvent dragEvent) {
             if (hasAddedTempItem) {
                 hasAddedTempItem = false;
                 getListView().getItems().remove(getPlaceholderItem());
             }
         }

         @Override
         protected boolean onDragDroppedFromOutsideSource(DragEvent dragEvent) {
                boolean success;

                if (!(dragEvent.getGestureSource() instanceof ListBuilderCell<?>)) {
                    success = false;
                } else {
                    Dragboard dragboard = dragEvent.getDragboard();

                    if (dragboard.hasContent(LIST_BUILDER_DATA_FORMAT)) {
                        ListBuilderCell<?> sourceCell = (ListBuilderCell<?>) dragEvent.getGestureSource();

                        ListView<?> listView = getListView();
                        ListView<?> sourceListView = sourceCell.getListView();
                        int indexOfPlaceHolder = listView.getItems().indexOf(getPlaceholderItem());

                        E sourceItem = (E) dragboard.getContent(LIST_BUILDER_DATA_FORMAT);

                        move(sourceListView, listView, List.of(sourceItem), indexOfPlaceHolder);

                        listView.getItems().remove(getPlaceholderItem());

                        hasAddedTempItem = false;
                        success = true;
                    } else {
                        success = false;
                    }
                }


                clearAndSelectElements(getListView(), (List<E>) List.of(draggedItem));

                return success;
         }

         @Override
         protected void onDragDetected(MouseEvent event) {
             Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);

             ClipboardContent content = new ClipboardContent();
             content.put(LIST_BUILDER_DATA_FORMAT, getItem());

             draggedItem = getItem();

             Image dragImage = createDragView(getItem(), isEmpty());

             dragboard.setDragView(dragImage);
             dragboard.setContent(content);
         }

         @Override
         protected void onDragDone(DragEvent dragEvent) {
             dragEvent.consume();
             setIsDropTargetCell(false);
             updateItem(getItem(), false);
         }

         @Override
         protected E getPlaceholderItem() {
             return listBuilder.getPlaceHolderItem();
         }
     }
}

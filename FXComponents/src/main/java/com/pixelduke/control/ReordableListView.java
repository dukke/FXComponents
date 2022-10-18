package com.pixelduke.control;
import javafx.css.PseudoClass;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.*;


public class ReordableListView<T> extends ListView<T> {
    private static final String DEFAULT_STYLE_CLASS = "reordable-list-view";

    public ReordableListView() {
        setCellFactory(param -> new DraggableCell<>());

        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /*=========================================================================*
     *                                                                         *
     *                      SUPPORTING CLASSES                                 *
     *                                                                         *
     *=========================================================================*/

    public static class DraggableCell<E> extends ListCell<E> {
        private static final PseudoClass DROP_TARGET_CELL_PSEUDO_CLASS = PseudoClass.getPseudoClass("drop-target");

        private static Object draggedItem;
        private static DraggableCell previousDropTargetCell;

        private static DraggableCell<?> previousDropFromOutsideSourceTargetCell;
        private static int previousDropFromOutsideSourceTargetIndex = -1;

        protected boolean isDropTargetCell = false;

        protected static boolean hasAddedTempItem = false;

        public DraggableCell() {
            setOnDragDetected(event -> {
                if (getItem() == null) {
                    return;
                }

                draggedItem = getItem();

                onDragDetected(event);

                setIsDropTargetCell(true);

                previousDropTargetCell = this;

                updateItem(getItem(), isEmpty());

                event.consume();
            });

            addEventHandler(DragEvent.DRAG_OVER, event -> {
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            });

            setOnDragDropped(event -> {
                boolean success = false;

                if (event.getGestureSource() instanceof DraggableCell) {
                    DraggableCell<?> sourceDraggableCell = (DraggableCell<?>) event.getGestureSource();
                    if (sourceDraggableCell.getListView() != getListView()) {
                        success = onDragDroppedFromOutsideSource(event);
                    } else {
                        // Drag and dropping within the same ListView. We are just re-ordering cells.
                        success = true;
                    }
                } else {
                    success = onDragDroppedFromOutsideSource(event);
                }

                event.setDropCompleted(success);

                event.consume();

                setIsDropTargetCell(false);

                // When the cell is dropped let's select it
                getListView().getSelectionModel().select(getIndex());

                updateItem(getItem(), isEmpty());
            });

            addEventHandler(DragEvent.DRAG_ENTERED, event -> {
                boolean dragEnteredFromOutsideSource = false;

                // We are dragging a cell so let's clear the ListView of all selected elements
                getListView().getSelectionModel().clearSelection();

                if (event.getGestureSource() instanceof DraggableCell) {
                    DraggableCell<?> sourceDraggableCell = (DraggableCell<?>) event.getGestureSource();
                    if (sourceDraggableCell.getListView() != getListView()) {
                        onDragEnteredFromOutsideSource(event);
                        dragEnteredFromOutsideSource = true;
                    }
                } else {
                    onDragEnteredFromOutsideSource(event);
                    dragEnteredFromOutsideSource = true;
                }

                if (!dragEnteredFromOutsideSource) {
                    // Still dragging a cell while remaining in this same ListView

                    ListView<E> listView = getListView();
                    E currItem = getItem();

                    int indexOfCurrItem = listView.getItems().indexOf(currItem);
                    int indexOfDraggedItem = listView.getItems().indexOf(draggedItem);

                    setIsDropTargetCell(true);
                    previousDropTargetCell.setIsDropTargetCell(false);

                    if (indexOfCurrItem == -1) {
                        // dragging into an empty cell
                        listView.getItems().remove(draggedItem);
                        listView.getItems().add((E) draggedItem);

                    } else if (indexOfDraggedItem > indexOfCurrItem) {
                        // dragging up
                        listView.getItems().set(indexOfCurrItem, (E) draggedItem);
                        listView.getItems().remove(indexOfDraggedItem);
                        listView.getItems().add(indexOfCurrItem + 1, currItem);
                    } else if (indexOfDraggedItem < indexOfCurrItem) {
                        // dragging down
                        listView.getItems().remove(draggedItem);
                        listView.getItems().add(indexOfCurrItem, (E) draggedItem); // indexOfCurrItem has decreased by 1 due to removing an item from the list so the index is where we want it now
                    }

                    updateItem(currItem, isEmpty());
                    previousDropTargetCell.updateItem(previousDropTargetCell.getItem(), previousDropTargetCell.isEmpty());

                    previousDropTargetCell = this;
                }
            });

            setOnDragExited(event -> {
                setPressed(false);
                setIsDropTargetCell(false);
            });

            setOnDragDone(this::onDragDone);
        }

        @Override
        protected void updateItem(E item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null || isDropTargetCell) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.toString());
                setGraphic(getGraphic());
            }
        }

        protected E getPlaceholderItem() {
            return null;
        }

        protected Image createDragView(E item, boolean empty) {
            return this.snapshot(new SnapshotParameters(), null);
        }

        protected void setIsDropTargetCell(boolean value) {
            this.isDropTargetCell = value;
            pseudoClassStateChanged(DROP_TARGET_CELL_PSEUDO_CLASS, value);
        }

        protected void onDragEnteredFromOutsideSource(DragEvent dragEvent) {
            ListView<E> listView = getListView();

            E currItem = getItem();
            int indexOfCurrItem = listView.getItems().indexOf(currItem);

            if (!hasAddedTempItem) {
                // FIRST time entering cells in this ListView while dragging from an outside source

                if (!isEmpty()) { // NOT Empty
                    listView.getItems().add(indexOfCurrItem, getPlaceholderItem());
                } else {          // Empty
                    listView.getItems().add(getPlaceholderItem());
                }
                hasAddedTempItem = true;

                previousDropFromOutsideSourceTargetCell = this;
                previousDropFromOutsideSourceTargetIndex = listView.getItems().indexOf(getItem());

                return;
            }


            if (previousDropFromOutsideSourceTargetCell != null && indexOfCurrItem != -1 && previousDropFromOutsideSourceTargetIndex != -1) {
                // NOT FIRST time entering cells in this ListView while dragging from an outside source

                if ((indexOfCurrItem < previousDropFromOutsideSourceTargetIndex)
                        && !isLastCellIndex(listView, indexOfCurrItem)) {
                    // Dragging up
                    listView.getItems().set(indexOfCurrItem + 1, currItem);
                    listView.getItems().set(indexOfCurrItem, getPlaceholderItem());
                } else if (indexOfCurrItem > previousDropFromOutsideSourceTargetIndex) {
                    // Dragging down
                    listView.getItems().set(indexOfCurrItem - 1, currItem);
                    listView.getItems().set(indexOfCurrItem, getPlaceholderItem());
                }
            }
            previousDropFromOutsideSourceTargetCell = this;
            previousDropFromOutsideSourceTargetIndex = indexOfCurrItem;

            updateItem(currItem, isEmpty());
        }

        private boolean isLastCellIndex(ListView<?> listView, int index) {
            return index == listView.getItems().size() - 1;
        }

        protected void onDragExitedFromOutsideSource(DragEvent dragEvent) {
        }

        protected boolean onDragDroppedFromOutsideSource(DragEvent dragEvent) {
            return false;
        }

        protected void onDragDetected(MouseEvent event) {
            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString(getItem().toString());



            Image dragImage = createDragView(getItem(), isEmpty());

            dragboard.setDragView(dragImage);
            dragboard.setContent(content);
        }

        protected void onDragDone(DragEvent dragEvent) {
            dragEvent.consume();
        }
    }
}

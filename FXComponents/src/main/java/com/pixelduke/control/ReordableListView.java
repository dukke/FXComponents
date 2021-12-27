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
        private static DraggableCell<?> previousDropTargetCell;

        protected boolean isDropTargetCell = false;

        private static final String tempPlaceholderItem = "ADDED FROM OUTSIDE SOURCE";
        private static boolean hasAddedTempItem = false;

        private static int previousDropTargetIndex = -1;

        public DraggableCell() {
            setOnDragDetected(event -> {
                if (getItem() == null) {
                    return;
                }

                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();
                content.putString(getItem().toString());

                draggedItem = getItem();

                Image dragImage = createDragView(getItem(), isEmpty());

                dragboard.setDragView(dragImage);
                dragboard.setContent(content);

                setIsDropTargetCell(true);

                previousDropTargetCell = this;

                updateItem(getItem(), isEmpty());

                event.consume();
            });

            addEventHandler(DragEvent.DRAG_OVER, event -> {
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            });

            setOnDragExited(event -> setPressed(false));

            setOnDragDropped(event -> {
                Dragboard dragBoard = event.getDragboard();
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

//                if (dragBoard.hasString()) {
//                    success = true;
//                }
                event.setDropCompleted(success);

                event.consume();

                setIsDropTargetCell(false);

                updateItem(getItem(), isEmpty());
            });

            addEventHandler(DragEvent.DRAG_ENTERED, event -> {
                boolean dragEnteredFromOutsideSource = false;

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

                if (event.getDragboard().hasString() && !dragEnteredFromOutsideSource) {
                    ListView<E> listView = getListView();
                    E currItem = getItem();

                    int indexOfCurrItem = listView.getItems().indexOf(currItem);
                    int indexOfDraggedItem = listView.getItems().indexOf(draggedItem);

                    setIsDropTargetCell(true);
                    previousDropTargetCell.setIsDropTargetCell(false);

                    if (indexOfDraggedItem > indexOfCurrItem) {
                        listView.getItems().set(indexOfCurrItem, (E) draggedItem);
                        listView.getItems().remove(indexOfDraggedItem);
                        listView.getItems().add(indexOfCurrItem + 1, currItem);
                    } else {
                        listView.getItems().set(indexOfCurrItem, (E) draggedItem);
                        listView.getItems().set(indexOfDraggedItem, currItem);
                    }

                    updateItem(currItem, isEmpty());

                    previousDropTargetCell = this;
                }
            });

            setOnDragDone(DragEvent::consume);
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
                listView.getItems().add(indexOfCurrItem, getPlaceholderItem());
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
                    listView.getItems().set(indexOfCurrItem, getPlaceholderItem());
                } else if (indexOfCurrItem < previousDropTargetIndex){
                    listView.getItems().set(indexOfCurrItem + 1, currItem);
                    listView.getItems().set(indexOfCurrItem, getPlaceholderItem());
                }
            } else {
                // First time entering this Node from outside
                if (indexOfCurrItem < listView.getItems().size() - 1) {
                    listView.getItems().add(indexOfCurrItem + 1, currItem);
                    listView.getItems().remove(tempPlaceholderItem);
                    listView.getItems().set(indexOfCurrItem, getPlaceholderItem());
                }
            }
            previousDropTargetCell = this;
            previousDropTargetIndex = indexOfCurrItem;

            updateItem(currItem, isEmpty());
        }

        protected void onDragExitedFromOutsideSource(DragEvent dragEvent) {

        }

        protected boolean onDragDroppedFromOutsideSource(DragEvent dragEvent) {
            return false;
        }
    }

}

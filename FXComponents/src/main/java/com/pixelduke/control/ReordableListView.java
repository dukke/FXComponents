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

        public DraggableCell() {
            setOnDragDetected(event -> {
                System.out.println("-------DRAG DETECTED");
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
                System.out.println("-DRAG OVER");
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            });

            setOnDragExited(event -> {
                System.out.println("-DRAG EXITED");
                setPressed(false);
            });

            setOnDragDropped(event -> {
                System.out.println("-DRAG DROPPED");
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

                System.out.println("--DRAG ENTERED");
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

        protected Image createDragView(E item, boolean empty) {
            return this.snapshot(new SnapshotParameters(), null);
        }

        protected void setIsDropTargetCell(boolean value) {
            this.isDropTargetCell = value;
            pseudoClassStateChanged(DROP_TARGET_CELL_PSEUDO_CLASS, value);
        }

        protected void onDragEnteredFromOutsideSource(DragEvent dragEvent) {

        }

        protected void onDragExitedFromOutsideSource(DragEvent dragEvent) {

        }

        protected boolean onDragDroppedFromOutsideSource(DragEvent dragEvent) {
            return false;
        }
    }

}

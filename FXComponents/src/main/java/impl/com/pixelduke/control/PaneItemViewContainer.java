package impl.com.pixelduke.control;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.OverrunStyle;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class PaneItemViewContainer extends Region implements PaneItemView {
    private static final Duration EXPAND_TRANSITION_DURATION = new Duration(100.0);
    private static final Duration ROTATE_TRANSITION_DURATION = new Duration(100.0);

    private final Menu menu;

    private final VBox childItemsContainer = new VBox();
    private final HBox titleContainer = new HBox();
    private final Label titleLabel = new Label();
    private final StackPane arrow = new StackPane();
    private final StackPane arrowContainer = new StackPane();

    private final ContextMenu contextMenu = new ContextMenu();

    private final ObjectProperty<Runnable> onSelectionRequested = new SimpleObjectProperty<>();

    private DoubleProperty transition;

    private final BooleanProperty showSelected = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, get());
        }
    };

    private final ReadOnlyBooleanWrapper hasGraphic = new ReadOnlyBooleanWrapper() {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(HAS_GRAPHIC_PSEUDOCLASS_STATE, get());
        }
    };

    private final BooleanProperty shrunken = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            if (get()) {
                titleLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                arrow.setManaged(false);
                arrow.setVisible(false);
            } else {
                titleLabel.setContentDisplay(ContentDisplay.LEFT);
                arrow.setManaged(true);
                arrow.setVisible(true);
            }

            if (get()) {
                collapseWithNoAnimation();

                if (isChildSelected()) {
                    fireOnMenuAction();
                }
            }
        }
    };

    private final ObservableList<PaneItemView> items = FXCollections.observableArrayList();

    private final BooleanProperty expanded = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            updateChildItemsVisibility();
        }
    };

    private double transitionStartValue;
    private Timeline timeline;
    private RotateTransition rotateTransition;

    private PaneItemViewContainer parentItemContainer;

    public PaneItemViewContainer(Menu menu, boolean shrunken) {
        this.menu = menu;
        this.shrunken.set(shrunken);

        hasGraphic.bind(graphicProperty().isNotNull());

        titleLabel.setTextOverrun(OverrunStyle.CLIP);

        // Selection
        StackPane selectionMarker = new StackPane();
        StackPane selectionMarkerContainer = new StackPane(selectionMarker);
        selectionMarkerContainer.getStyleClass().add("selection-marker-container");
        selectionMarker.getStyleClass().add("selection-marker");

        arrow.setMaxWidth(Region.USE_PREF_SIZE);
        arrow.setMaxHeight(Region.USE_PREF_SIZE);

        arrowContainer.getChildren().add(arrow);
        titleContainer.getChildren().addAll(selectionMarkerContainer, titleLabel, arrowContainer);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(titleContainer, childItemsContainer);

        rotateTransition = new RotateTransition(ROTATE_TRANSITION_DURATION, arrowContainer);
        rotateTransition.setCycleCount(1);

        items.addListener(this::onItemsChanged);

        titleContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClickedOnTitle);

        updateChildItemsVisibility();

        contextMenu.getItems().addAll(menu.getItems());

        // CSS
        getStyleClass().addAll("navigation-pane-item", "container-item-view");
        childItemsContainer.getStyleClass().add("child-items-container");
        titleContainer.getStyleClass().add("item-container");
        arrowContainer.getStyleClass().add("arrow-container");
        arrow.getStyleClass().add("arrow");
    }

    private void collapseWithNoAnimation() {
        setExpanded(false);
        arrowContainer.setRotate(0);
        setTransition(0);
    }

    private void doAnimation() {
        Duration duration;
        if (timeline != null && (timeline.getStatus() != Animation.Status.STOPPED)) {
            duration = timeline.getCurrentTime();
            timeline.stop();
        } else {
            duration = EXPAND_TRANSITION_DURATION;
        }

        timeline = new Timeline();
        timeline.setCycleCount(1);

        KeyFrame k1, k2;

        transitionStartValue = getTransition();

        if (isExpanded()) {
            k1 = new KeyFrame(
                    Duration.ZERO,
                    new KeyValue(transitionProperty(), transitionStartValue)
            );

            k2 = new KeyFrame(
                    duration,
                    new KeyValue(transitionProperty(), 1, Interpolator.LINEAR)

            );

            rotateTransition.setToAngle(180);
        } else {
            k1 = new KeyFrame(
                    Duration.ZERO,
                    new KeyValue(transitionProperty(), transitionStartValue)
            );

            k2 = new KeyFrame(
                    duration,
                    new KeyValue(transitionProperty(), 0, Interpolator.LINEAR)
            );
            rotateTransition.setToAngle(0);
        }

        timeline.getKeyFrames().setAll(k1, k2);
        timeline.play();

        rotateTransition.play();
    }

    private boolean isChildSelected() {
        for (PaneItemView paneItemView : items) {
            if (paneItemView.isShowSelected()) {
                return true;
            }
        }

        return false;
    }

    private void onMouseClickedOnTitle(MouseEvent mouseEvent) {
        if (!isShrunken()) {
            setExpanded(!expanded.get());
            doAnimation();
        } else {
            contextMenu.show(this, Side.RIGHT, 0, 0);
        }

        fireOnMenuAction();
    }

    private void fireOnMenuAction() {
        menu.fire();
    }

    private void updateChildItemsVisibility() {
        childItemsContainer.setManaged(expanded.get());
        childItemsContainer.setVisible(expanded.get());
    }

    private void onItemsChanged(ListChangeListener.Change<? extends PaneItemView> change) {
        while(change.next()) {
            if (change.wasAdded()) {
                for (PaneItemView addedItem : change.getAddedSubList()) {
                    childItemsContainer.getChildren().add(addedItem.getNodeRepresentation());
                    addedItem.setParentItemView(this);
                }
            }
            if (change.wasRemoved()) {
                for (PaneItemView removedItem : change.getRemoved()) {
                    removedItem.setParentItemView(null);
                    childItemsContainer.getChildren().remove(removedItem.getNodeRepresentation());
                }
            }
        }
    }

    @Override
    protected double computePrefHeight(double width) {
        double prefHeight = 0;

        prefHeight += titleContainer.prefHeight(width);
        if (!isShrunken()) {
            prefHeight += childItemsContainer.prefHeight(width) * getTransition();
        }

        return prefHeight;
    }

    @Override
    protected void layoutChildren() {
        double leftPadding = snappedLeftInset();
        double topPadding = snappedTopInset();
        double rightPadding = snappedRightInset();

        double width = getWidth();

        double availableWidth = width - leftPadding - rightPadding;

        double titlePrefWidth = availableWidth;
        double titlePrefHeight = titleContainer.prefHeight(titlePrefWidth);

        titleContainer.resizeRelocate(leftPadding, topPadding, titlePrefWidth, titlePrefHeight);

        double childItemsPrefWidth = availableWidth;
        double childItemsHeight = childItemsContainer.prefHeight(childItemsPrefWidth) * getTransition();

        childItemsContainer.resizeRelocate(leftPadding, titlePrefHeight, childItemsPrefWidth, childItemsHeight);
    }

    /* *************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    // -- transition
    private final void setTransition(double value) { transitionProperty().set(value); }
    private final double getTransition() { return transition == null ? 0.0 : transition.get(); }
    private final DoubleProperty transitionProperty() {
        if (transition == null) {
            transition = new SimpleDoubleProperty(this, "transition", 0.0) {
                @Override protected void invalidated() { requestLayout();}
            };
        }
        return transition;
    }

    // -- items
    ObservableList<PaneItemView> getItems() { return items; }

    // -- title
    @Override
    public String getTitle() { return titleLabel.getText(); }
    @Override
    public StringProperty titleProperty() { return titleLabel.textProperty(); }
    @Override
    public void setTitle(String title) { titleLabel.setText(title); }

    // -- graphic
    @Override
    public Node getGraphic() { return titleLabel.getGraphic(); }
    @Override
    public ObjectProperty<Node> graphicProperty() { return titleLabel.graphicProperty(); }
    @Override
    public void setGraphic(Node graphic) { titleLabel.setGraphic(graphic); }


    // -- has graphic
    @Override
    public ReadOnlyBooleanProperty hasGraphicProperty() { return hasGraphicProperty();}
    public boolean getHasGraphic() { return hasGraphic.get(); }

    // selected
    @Override
    public void setShowSelected(boolean value) { showSelected.set(value); }
    @Override
    public boolean isShowSelected() { return showSelected.get(); }
    @Override
    public BooleanProperty showSelectedProperty() { return showSelected; }

    // -- shrunken
    @Override
    public boolean isShrunken() { return shrunken.get(); }
    @Override
    public void setShrunken(boolean value) { shrunken.set(value); }
    @Override
    public BooleanProperty shrunkenProperty() { return shrunken; }

    // -- node representation
    @Override
    public Node getNodeRepresentation() { return this; }

    // -- expanded
    public boolean isExpanded() { return expanded.get(); }
    public BooleanProperty expandedProperty() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded.set(expanded); }

    // -- parent
    @Override
    public PaneItemViewContainer getParentItemView() { return parentItemContainer; }
    @Override
    public void setParentItemView(PaneItemViewContainer parent) { this.parentItemContainer = parent; }
}

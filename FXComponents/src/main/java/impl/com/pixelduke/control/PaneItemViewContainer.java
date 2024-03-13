package impl.com.pixelduke.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PaneItemViewContainer extends Region implements PaneItemView {
    private final VBox mainContainer = new VBox();
    private final VBox childItemsContainer = new VBox();
    private final HBox titleContainer = new HBox();
    private final Label titleLabel = new Label();
    private final StackPane arrow = new StackPane();
    private final StackPane arrowContainer = new StackPane();

    private final ContextMenu contextMenu = new ContextMenu();

    private final ObjectProperty<Runnable> onSelectionRequested = new SimpleObjectProperty<>();

    private final BooleanProperty selected = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, get());
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
                setExpanded(false);
                if (isChildSelected()) {
                    fireOnSelectionRequest();
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

    public PaneItemViewContainer(Menu menu, boolean shrunken) {
        this.shrunken.set(shrunken);

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

        mainContainer.getChildren().addAll(titleContainer, childItemsContainer);

        getChildren().add(mainContainer);

        items.addListener(this::onItemsChanged);

        titleContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClickedOnTitle);

        updateChildItemsVisibility();

        contextMenu.getItems().addAll(menu.getItems());

        // CSS
        getStyleClass().addAll("navigation-pane-item", "container-item-view");
        mainContainer.getStyleClass().add("main-container");
        childItemsContainer.getStyleClass().add("child-items-container");
        titleContainer.getStyleClass().add("item-container");
        arrowContainer.getStyleClass().add("arrow-container");
        arrow.getStyleClass().add("arrow");
    }

    private boolean isChildSelected() {
        for (PaneItemView paneItemView : items) {
            if (paneItemView.isSelected()) {
                return true;
            }
        }

        return false;
    }

    private void onMouseClickedOnTitle(MouseEvent mouseEvent) {
        if (!isShrunken()) {
            setExpanded(!expanded.get());
        } else {
            contextMenu.show(this, Side.RIGHT, 0, 0);
        }

        fireOnSelectionRequest();
    }

    private void fireOnSelectionRequest() {
        if (getOnSelectionRequested() != null) {
            getOnSelectionRequested().run();
        }
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
                }
            }
            if (change.wasRemoved()) {
                for (PaneItemView removedItem : change.getRemoved()) {
                    childItemsContainer.getChildren().remove(removedItem.getNodeRepresentation());
                }
            }
        }
    }

    @Override
    protected void layoutChildren() {
        double leftPadding = snappedLeftInset();
        double topPadding = snappedTopInset();
        double rightPadding = snappedRightInset();
        double bottomPadding = snappedBottomInset();

        double width = getWidth();
        double height = getHeight();

        double availableWidth = width - leftPadding - rightPadding;
        double availableHeight = height - topPadding - bottomPadding;

        mainContainer.resizeRelocate(leftPadding, topPadding, availableWidth, availableHeight);
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

    // selected
    @Override
    public void setSelected(boolean value) { selected.set(value); }
    @Override
    public boolean isSelected() { return selected.get(); }
    @Override
    public BooleanProperty selectedProperty() { return selected; }

    // -- on selection requested
    @Override
    public Runnable getOnSelectionRequested() { return onSelectionRequested.get(); }
    @Override
    public ObjectProperty<Runnable> onSelectionRequestedProperty() { return onSelectionRequested; }
    public void setOnSelectionRequested(Runnable onSelectionRequested) { this.onSelectionRequested.set(onSelectionRequested); }

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
    public void setExpanded(boolean expanded) { this.expanded.set(expanded);}
}

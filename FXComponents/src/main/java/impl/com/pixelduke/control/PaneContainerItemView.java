package impl.com.pixelduke.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PaneContainerItemView extends Region implements PaneItemView {
    private final VBox mainContainer = new VBox();
    private final HBox titleContainer = new HBox();
    private final Label titleLabel = new Label();
    private final StackPane arrow = new StackPane();
    private final StackPane arrowContainer = new StackPane();

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
        }
    };

    private final ObservableList<PaneItemView> items = FXCollections.observableArrayList();

    public PaneContainerItemView(boolean shrunken) {
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

        mainContainer.getChildren().add(titleContainer);

        getChildren().add(mainContainer);

        items.addListener(this::onItemsChanged);

        // CSS
        getStyleClass().addAll("navigation-pane-item", "container-item-view");
        mainContainer.getStyleClass().add("main-container");
        titleContainer.getStyleClass().add("item-container");
        arrowContainer.getStyleClass().add("arrow-container");
        arrow.getStyleClass().add("arrow");
    }

    private void onItemsChanged(ListChangeListener.Change<? extends PaneItemView> change) {
        while(change.next()) {
            if (change.wasAdded()) {
                for (PaneItemView addedItem : change.getAddedSubList()) {
                    mainContainer.getChildren().add(addedItem.getNodeRepresentation());
                }
            }
            if (change.wasRemoved()) {
                for (PaneItemView removedItem : change.getRemoved()) {
                    mainContainer.getChildren().remove(removedItem.getNodeRepresentation());
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
    public ObservableList<PaneItemView> getItems() { return items; }

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
}

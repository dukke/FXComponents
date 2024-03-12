package impl.com.pixelduke.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class PaneItemViewLeaf extends HBox implements PaneItemView {
    private final Label label = new Label();

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
                label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                label.setContentDisplay(ContentDisplay.LEFT);
            }
        }
    };

    private final ObjectProperty<Runnable> onSelectionRequested = new SimpleObjectProperty<>();

    public PaneItemViewLeaf(boolean shrunken) {
        this.shrunken.set(shrunken);

        getStyleClass().addAll("navigation-pane-item", "item-container");

        // Selection
        StackPane selectionMarker = new StackPane();
        StackPane selectionMarkerContainer = new StackPane(selectionMarker);
        selectionMarkerContainer.getStyleClass().add("selection-marker-container");
        selectionMarker.getStyleClass().add("selection-marker");

        getChildren().addAll(selectionMarkerContainer, label);

        addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        if (getOnSelectionRequested() != null) {
            getOnSelectionRequested().run();
        }
    }

    // -- on selection requested
    public Runnable getOnSelectionRequested() { return onSelectionRequested.get(); }
    public ObjectProperty<Runnable> onSelectionRequestedProperty() { return onSelectionRequested; }
    public void setOnSelectionRequested(Runnable onSelectionRequested) { this.onSelectionRequested.set(onSelectionRequested); }

    // -- shrunken
    public boolean isShrunken() { return shrunken.get(); }
    public BooleanProperty shrunkenProperty() { return shrunken; }
    public void setShrunken(boolean expanded) { this.shrunken.set(expanded); }

    // -- title
    @Override
    public String getTitle() { return label.getText(); }
    @Override
    public StringProperty titleProperty() { return label.textProperty(); }
    @Override
    public void setTitle(String title) { label.setText(title); }

    // -- graphic
    @Override
    public Node getGraphic() { return label.getGraphic(); }
    @Override
    public ObjectProperty<Node> graphicProperty() { return label.graphicProperty(); }
    @Override
    public void setGraphic(Node graphic) { label.setGraphic(graphic); }

    // -- selected
    public boolean isSelected() { return selected.get(); }
    @Override
    public BooleanProperty selectedProperty() { return selected; }
    public void setSelected(boolean selected) { this.selected.set(selected); }

    // -- node representation
    @Override
    public Node getNodeRepresentation() { return this; }

}

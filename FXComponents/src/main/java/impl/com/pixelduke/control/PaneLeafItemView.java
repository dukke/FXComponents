package impl.com.pixelduke.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class PaneLeafItemView extends HBox implements PaneItemView {
    private final Label label = new Label();

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

    public PaneLeafItemView(boolean shrunken) {
        this.shrunken.set(shrunken);

        getStyleClass().addAll("navigation-pane-item", "item-container");

        // Selection
        StackPane selectionMarker = new StackPane();
        StackPane selectionMarkerContainer = new StackPane(selectionMarker);
        selectionMarkerContainer.getStyleClass().add("selection-marker-container");
        selectionMarker.getStyleClass().add("selection-marker");

        getChildren().addAll(selectionMarkerContainer, label);
    }

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

    // -- node representation
    @Override
    public Node getNodeRepresentation() { return this; }

}

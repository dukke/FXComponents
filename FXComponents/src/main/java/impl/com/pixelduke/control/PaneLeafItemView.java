package impl.com.pixelduke.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class PaneLeafItemView extends HBox implements PaneItemView {
    private Label label = new Label();

    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<Node> graphic = new SimpleObjectProperty<>();

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

    public PaneLeafItemView(boolean expanded) {
        this.shrunken.set(expanded);

        getStyleClass().add("item-container");

        // Selection
        StackPane selectionMarker = new StackPane();
        StackPane selectionMarkerContainer = new StackPane(selectionMarker);
        selectionMarkerContainer.getStyleClass().add("selection-marker-container");
        selectionMarker.getStyleClass().add("selection-marker");

        // label
        label.textProperty().bind(titleProperty());
        label.graphicProperty().bind(graphicProperty());

        getChildren().addAll(selectionMarkerContainer, label);
    }

    // -- shrunken
    public boolean isShrunken() { return shrunken.get(); }
    public BooleanProperty shrunkenProperty() { return shrunken; }
    public void setShrunken(boolean expanded) { this.shrunken.set(expanded); }

    // -- title
    @Override
    public String getTitle() { return title.get(); }
    @Override
    public StringProperty titleProperty() { return title; }
    @Override
    public void setTitle(String title) { this.title.set(title); }

    // -- graphic
    @Override
    public Node getGraphic() { return graphic.get(); }
    @Override
    public ObjectProperty<Node> graphicProperty() { return graphic; }
    @Override
    public void setGraphic(Node graphic) { this.graphic.set(graphic); }

    // -- node representation
    @Override
    public Node getNodeRepresentation() { return this; }

}

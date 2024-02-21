package impl.com.pixelduke.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;

interface PaneItemView {
    PseudoClass SELECTED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("selected");

    // -- title
    String getTitle();
    StringProperty titleProperty();
    void setTitle(String title);

    // -- graphic
    Node getGraphic();
    ObjectProperty<Node> graphicProperty();
    void setGraphic(Node graphic);

    // -- selected
    void setSelected(boolean value);
    boolean isSelected();
    BooleanProperty selectedProperty();

    // -- on selection requested
    Runnable getOnSelectionRequested();
    ObjectProperty<Runnable> onSelectionRequestedProperty();
    void setOnSelectionRequested(Runnable onSelectionRequested);

    // -- shrunken
    boolean isShrunken();
    void setShrunken(boolean value);
    BooleanProperty shrunkenProperty();

    // -- node representation
    Node getNodeRepresentation();
}

package impl.com.pixelduke.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

public interface PaneItemView {
    // -- title
    String getTitle();
    StringProperty titleProperty();
    void setTitle(String title);

    // -- graphic
    Node getGraphic();
    ObjectProperty<Node> graphicProperty();
    void setGraphic(Node graphic);

    // -- shrunken
    boolean isShrunken();
    void setShrunken(boolean value);
    BooleanProperty shrunkenProperty();

    // -- node representation
    Node getNodeRepresentation();
}

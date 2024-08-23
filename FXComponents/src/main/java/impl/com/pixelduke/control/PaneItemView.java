package impl.com.pixelduke.control;

import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.Node;

interface PaneItemView {
    PseudoClass SELECTED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("selected");
    PseudoClass HAS_GRAPHIC_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("has-graphic");

    // -- title
    String getTitle();
    StringProperty titleProperty();
    void setTitle(String title);

    // -- graphic
    Node getGraphic();
    ObjectProperty<Node> graphicProperty();
    void setGraphic(Node graphic);

    boolean getHasGraphic();
    ReadOnlyBooleanProperty hasGraphicProperty();

    // -- selected
    void setShowSelected(boolean value);
    boolean isShowSelected();
    BooleanProperty showSelectedProperty();

    // -- shrunken
    boolean isShrunken();
    void setShrunken(boolean value);
    BooleanProperty shrunkenProperty();

    // -- node representation
    Node getNodeRepresentation();

    // -- parent
    PaneItemViewContainer getParentItemView();
    void setParentItemView(PaneItemViewContainer parent);
}

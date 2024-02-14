package impl.com.pixelduke.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public class PaneContainerItemView extends Region implements PaneItemView{
    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public StringProperty titleProperty() {
        return null;
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public Node getGraphic() {
        return null;
    }

    @Override
    public ObjectProperty<Node> graphicProperty() {
        return null;
    }

    @Override
    public void setGraphic(Node graphic) {

    }

    @Override
    public boolean isShrunken() {
        return false;
    }

    @Override
    public void setShrunken(boolean value) {

    }

    @Override
    public BooleanProperty shrunkenProperty() {
        return null;
    }

    @Override
    public Node getNodeRepresentation() {
        return null;
    }
}

package impl.com.pixelduke.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;

public class NavigationPaneLeftPane extends Region {
    private static final PseudoClass SELECTED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("selected");
    private static final PseudoClass EXPANDED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("expanded");

    private static final String HAMBURGER_ICON_URL = NavigationPaneSkin.class.getResource("hamburguer_icon.png").toExternalForm();
    private static final String SETTINGS_ICON_URL = NavigationPaneSkin.class.getResource("settings_icon.png").toExternalForm();

    private final VBox topContainer = new VBox();
    private final VBox footerContainer = new VBox();
    private final VBox menuItemsContainer = new VBox();
    private final VBox settingsContainer = new VBox();
    private final ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();

    private final HashMap<MenuItem, ItemView> menuItemVisualRepresentation = new HashMap<>();

    private final ObjectProperty<MenuItem> selectedMenuItem = new SimpleObjectProperty<>();

    private final BooleanProperty expanded = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            onExpandedChanged();
        }
    };

    private Node settingsItemNode;

    private Node previouslySelectedMenuItem;

    public NavigationPaneLeftPane() {
        // expanded state
        expanded.set(true);
        pseudoClassStateChanged(EXPANDED_PSEUDOCLASS_STATE, expanded.get());

        // hamburger button
        ImageView hamburguerImageView = new ImageView(HAMBURGER_ICON_URL);
        Button hamburguerButton = new Button();
        hamburguerButton.setOnMouseClicked(this::onHambugerButtonClicked);
        hamburguerButton.getStyleClass().add("light");
        hamburguerButton.setGraphic(hamburguerImageView);
        hamburguerButton.setFocusTraversable(false);

        HBox hamburgerContainer = new HBox();
        hamburgerContainer.getStyleClass().add("hamburger-container");
        hamburgerContainer.getChildren().add(hamburguerButton);

        // settings item
        ImageView settingsImageView = new ImageView(SETTINGS_ICON_URL);
        MenuItem settingsMenuItem = new MenuItem("Settings", settingsImageView);
        settingsItemNode = createItemRepresentation(settingsMenuItem);
        settingsContainer.getChildren().add(settingsItemNode);


        getChildren().addAll(topContainer, footerContainer, settingsContainer);

        topContainer.getChildren().add(hamburgerContainer);
        topContainer.getChildren().add(menuItemsContainer);

        menuItems.addListener(this::onMenuItemsChanged);

        // CSS
        getStyleClass().add("pane");
        topContainer.getStyleClass().add("top-container");
        menuItemsContainer.getStyleClass().add("menu-items-container");
        settingsContainer.getStyleClass().add("settings-container");
        hamburguerButton.getStyleClass().add("hamburger");
    }

    private void onHambugerButtonClicked(MouseEvent mouseEvent) {
        expanded.set(!expanded.get());
    }

    private void onExpandedChanged() {
        for (ItemView itemView : menuItemVisualRepresentation.values()) {
            itemView.setExpanded(expanded.get());
        }
        pseudoClassStateChanged(EXPANDED_PSEUDOCLASS_STATE, expanded.get());
        requestLayout();
    }


    public ObservableList<MenuItem> getMenuItems() { return menuItems; }

    private void onMenuItemsChanged(ListChangeListener.Change<? extends MenuItem> change) {
        while(change.next()) {
            if (change.wasAdded()) {
                for (MenuItem addedMenuItem : change.getAddedSubList()) {
                    Node menuItemNode = createItemRepresentation(addedMenuItem);
                    menuItemsContainer.getChildren().add(menuItemNode);
                }
            }
            if (change.wasRemoved()) {
                for (MenuItem removedMenuItem : change.getRemoved()) {
                    Node nodeToRemove = menuItemVisualRepresentation.get(removedMenuItem);
                    menuItemsContainer.getChildren().remove((nodeToRemove));
                }
            }
        }
    }

    private Node createItemRepresentation(MenuItem menuItem) {
        ItemView itemView = new ItemView(expanded.get());
        itemView.label.textProperty().bind(menuItem.textProperty());
        itemView.label.graphicProperty().bind(menuItem.graphicProperty());

        menuItemVisualRepresentation.put(menuItem, itemView);

        // mouse events
        itemView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> onMouseClickedOnMenuItem(event, itemView, menuItem));

        return itemView;
    }

    private void onMouseClickedOnMenuItem(MouseEvent mouseEvent, Node menuItemContainer, MenuItem menuItem) {
        menuItemContainer.pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, true);

        if (previouslySelectedMenuItem != null) {
            previouslySelectedMenuItem.pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, false);
        }
        previouslySelectedMenuItem = menuItemContainer;

        selectedMenuItem.set(menuItem);
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

        topContainer.resize(availableWidth, availableHeight);

        double settingsNodeHeight = settingsContainer.prefHeight(availableWidth);
        settingsContainer.resizeRelocate(0, availableHeight - settingsNodeHeight, availableWidth, settingsNodeHeight);
    }

    // -- selected menu item
    public MenuItem getSelectedMenuItem() { return selectedMenuItem.get(); }
    public ObjectProperty<MenuItem> selectedMenuItemProperty() { return selectedMenuItem; }
    public void setSelectedMenuItem(MenuItem selectedMenuItem) { this.selectedMenuItem.set(selectedMenuItem); }

    // -- expanded
    public boolean isExpanded() { return expanded.get(); }
    public BooleanProperty expandedProperty() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded.set(expanded); }


    /*=========================================================================*
     *                                                                         *
     *                      SUPPORTING CLASSES                                 *
     *                                                                         *
     *=========================================================================*/

    private static class ItemView extends HBox {
        Label label = new Label();
        private BooleanProperty expanded = new SimpleBooleanProperty() {
            @Override
            protected void invalidated() {
                if (get()) {
                    label.setContentDisplay(ContentDisplay.LEFT);
                } else {
                    label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        };

        ItemView(boolean expanded) {
            this.expanded.set(expanded);

            getStyleClass().add("item-container");

            // Selection
            StackPane selectionMarker = new StackPane();
            StackPane selectionMarkerContainer = new StackPane(selectionMarker);
            selectionMarkerContainer.getStyleClass().add("selection-marker-container");
            selectionMarker.getStyleClass().add("selection-marker");

            getChildren().addAll(selectionMarkerContainer, label);
        }

        // -- expanded
        public boolean isExpanded() { return expanded.get(); }
        public BooleanProperty expandedProperty() { return expanded; }
        public void setExpanded(boolean expanded) { this.expanded.set(expanded); }
    }
}
package impl.com.pixelduke.control;

import com.pixelduke.control.NavigationPane;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.HashMap;

public class NavigationPaneLeftPane extends Region {
    private static final PseudoClass SHRUNKEN_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("shrunken");

    private static final String HAMBURGER_ICON_URL = NavigationPaneSkin.class.getResource("hamburger_icon.png").toExternalForm();
    private static final String SETTINGS_ICON_URL = NavigationPaneSkin.class.getResource("settings_icon.png").toExternalForm();

    private final VBox topContainer = new VBox();
    private final VBox footerContainer = new VBox();
    private final VBox menuItemsContainer = new VBox();
    private final VBox settingsContainer = new VBox();
    private final ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    private final ObservableList<MenuItem> footerMenuItems = FXCollections.observableArrayList();

    private final HashMap<MenuItem, PaneItemView> menuItemVisualRepresentation = new HashMap<>();

    private final ObjectProperty<MenuItem> selectedMenuItem = new SimpleObjectProperty<>();

    private final BooleanProperty shrunken = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            onShrunkenChanged();
        }
    };

    private final PaneItemView settingsItem;

    private PaneItemView previouslySelectedMenuItem;

    private final NavigationPane navigationPane;

    public NavigationPaneLeftPane(NavigationPane navigationPane) {
        this.navigationPane = navigationPane;

        // shrunken state
        shrunken.set(false);
        prefWidthProperty().bind(navigationPane.unshrunkenWidthProperty());

        pseudoClassStateChanged(SHRUNKEN_PSEUDOCLASS_STATE, shrunken.get());

        // hamburger button
        ImageView hamburguerImageView = new ImageView(HAMBURGER_ICON_URL);
        Button hamburguerButton = new Button();
        hamburguerButton.setOnMouseClicked(this::onHamburgerButtonClicked);
        hamburguerButton.getStyleClass().add("light");
        hamburguerButton.setGraphic(hamburguerImageView);
        hamburguerButton.setFocusTraversable(false);

        HBox hamburgerContainer = new HBox();
        hamburgerContainer.getStyleClass().add("hamburger-container");
        hamburgerContainer.getChildren().add(hamburguerButton);

        // settings item
        ImageView settingsImageView = new ImageView(SETTINGS_ICON_URL);
        MenuItem settingsMenuItem = new MenuItem("Settings", settingsImageView);
        settingsItem = createItemRepresentation(settingsMenuItem);
        settingsContainer.getChildren().add(settingsItem.getNodeRepresentation());


        getChildren().addAll(topContainer, footerContainer, settingsContainer);

        topContainer.getChildren().add(hamburgerContainer);
        topContainer.getChildren().add(menuItemsContainer);

        menuItems.addListener(this::onMenuItemsChanged);
        footerMenuItems.addListener(this::onFooterMenuItemsChanged);

        // CSS
        getStyleClass().add("pane");
        topContainer.getStyleClass().add("top-container");
        menuItemsContainer.getStyleClass().add("menu-items-container");
        footerContainer.getStyleClass().add("footer-items-container");
        settingsContainer.getStyleClass().add("settings-container");
        hamburguerButton.getStyleClass().add("hamburger");
    }

    private void onHamburgerButtonClicked(MouseEvent mouseEvent) {
        shrunken.set(!shrunken.get());
    }

    private void onShrunkenChanged() {
        Timeline fadeTimeline = new Timeline();
        fadeTimeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(50), actionEvent -> {
            for (PaneItemView itemView : menuItemVisualRepresentation.values()) {
                itemView.setShrunken(shrunken.get());
            }
            pseudoClassStateChanged(SHRUNKEN_PSEUDOCLASS_STATE, shrunken.get());
        }));

        prefWidthProperty().unbind();
        Timeline shrinkTimeline = new Timeline();
        if (shrunken.get()) {
            shrinkTimeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(100),
                                                              new KeyValue(prefWidthProperty(),
                                                              navigationPane.getShrunkenWidth(),
                                                              Interpolator.EASE_OUT)));
        } else {
            shrinkTimeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(100),
                                                 new KeyValue(prefWidthProperty(),
                                                 navigationPane.getUnshrunkenWidth(),
                                                 Interpolator.EASE_IN)));
        }

        ParallelTransition parallelTransition = new ParallelTransition(shrinkTimeline, fadeTimeline);
        parallelTransition.playFromStart();
    }

    // -- menu items
    public ObservableList<MenuItem> getMenuItems() { return menuItems; }

    // -- footer menu items
    public ObservableList<MenuItem> getFooterMenuItems() { return footerMenuItems; }

    private void onMenuItemsChanged(ListChangeListener.Change<? extends MenuItem> change) {
        while(change.next()) {
            if (change.wasAdded()) {
                for (MenuItem addedMenuItem : change.getAddedSubList()) {
                    Node menuItemNode = createItemRepresentation(addedMenuItem).getNodeRepresentation();
                    menuItemsContainer.getChildren().add(menuItemNode);
                }
            }
            if (change.wasRemoved()) {
                for (MenuItem removedMenuItem : change.getRemoved()) {
                    Node nodeToRemove = menuItemVisualRepresentation.get(removedMenuItem).getNodeRepresentation();
                    menuItemsContainer.getChildren().remove((nodeToRemove));
                }
            }
        }
    }

    private void onFooterMenuItemsChanged(ListChangeListener.Change<? extends MenuItem> change) {
        while(change.next()) {
            if (change.wasAdded()) {
                for (MenuItem addedMenuItem : change.getAddedSubList()) {
                    Node menuItemNode = createItemRepresentation(addedMenuItem).getNodeRepresentation();
                    footerContainer.getChildren().add(menuItemNode);
                }
            }
            if (change.wasRemoved()) {
                for (MenuItem removedMenuItem : change.getRemoved()) {
                    Node nodeToRemove = menuItemVisualRepresentation.get(removedMenuItem).getNodeRepresentation();
                    footerContainer.getChildren().remove((nodeToRemove));
                }
            }
        }
    }

    private PaneItemView createItemRepresentation(MenuItem menuItem) {
        PaneItemView itemView;
        if (menuItem instanceof Menu menu) {
            PaneItemViewContainer paneItemViewContainer = new PaneItemViewContainer(menu, shrunken.get());

            for (MenuItem childMenuItem : menu.getItems()) {
                PaneItemView paneItemView = createItemRepresentation(childMenuItem);
                paneItemViewContainer.getItems().add(paneItemView);
            }

            itemView = paneItemViewContainer;

        } else {
            itemView = new PaneItemViewLeaf(shrunken.get());
        }

        itemView.titleProperty().bind(menuItem.textProperty());
        itemView.graphicProperty().bind(menuItem.graphicProperty());

        menuItemVisualRepresentation.put(menuItem, itemView);

        // selection request events
        itemView.setOnSelectionRequested(() -> onSelectionRequestedOnItem(itemView, menuItem));

        return itemView;
    }

    private void onSelectionRequestedOnItem(PaneItemView itemView, MenuItem menuItem) {
        if (previouslySelectedMenuItem != null) {
            previouslySelectedMenuItem.setSelected(false);
        }
        previouslySelectedMenuItem = itemView;
        itemView.setSelected(true);

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

        double settingsContainerHeight = settingsContainer.prefHeight(availableWidth);
        double footerContainerHeight = footerContainer.prefHeight(availableWidth);

        // topContainer
        topContainer.resize(availableWidth, availableHeight - settingsContainerHeight - footerContainerHeight);

        // footer container
        double footerContainerY = availableHeight - settingsContainerHeight - footerContainerHeight;
        footerContainer.resizeRelocate(0, footerContainerY, availableWidth, footerContainerHeight);

        // settings container
        double settingsContainerY = availableHeight - settingsContainerHeight;
        settingsContainer.resizeRelocate(0, settingsContainerY, availableWidth, settingsContainerHeight);
    }

    // -- selected menu item
    public MenuItem getSelectedMenuItem() { return selectedMenuItem.get(); }
    public ObjectProperty<MenuItem> selectedMenuItemProperty() { return selectedMenuItem; }
    public void setSelectedMenuItem(MenuItem selectedMenuItem) { this.selectedMenuItem.set(selectedMenuItem); }

    // -- shrunken
    public boolean getShrunken() { return shrunken.get(); }
    public BooleanProperty shrunkenProperty() { return shrunken; }
    public void setShrunken(boolean shrunken) { this.shrunken.set(shrunken); }
}
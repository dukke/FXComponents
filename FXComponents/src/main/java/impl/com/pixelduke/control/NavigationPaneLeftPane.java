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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class NavigationPaneLeftPane extends Region {
    private static final PseudoClass SHRUNKEN_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("shrunken");

    private static final String HAMBURGER_ICON_URL = NavigationPaneSkin.class.getResource("hamburger_icon.png").toExternalForm();


    private final VBox topContainer = new VBox();
    private final VBox footerContainer = new VBox();
    private final ScrollPane menuItemsScrollPane = new ScrollPane();
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

    private final Map<MenuItem, EventHandler<ActionEvent>> menuItemsToActionListeners = new HashMap();

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
        MenuItem settingsMenuItem = navigationPane.getSettingsMenuItem();
        settingsItem = createSettingsItem(settingsMenuItem);

        addMenuItemListener(settingsMenuItem);
        settingsContainer.getChildren().add(settingsItem.getNodeRepresentation());

        navigationPane.settingsVisibleProperty().addListener(observable -> updateSettingsVisibility());
        updateSettingsVisibility();


        getChildren().addAll(topContainer, footerContainer, settingsContainer);

        menuItemsScrollPane.setContent(menuItemsContainer);
        menuItemsScrollPane.setFitToWidth(true);
        menuItemsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        topContainer.getChildren().add(hamburgerContainer);
        topContainer.getChildren().add(menuItemsScrollPane);

        menuItems.addListener((ListChangeListener<? super MenuItem>) change -> onItemsChanged(change, false));
        footerMenuItems.addListener((ListChangeListener<? super MenuItem>) change -> onItemsChanged(change, true));

        // CSS
        getStyleClass().add("pane");
        topContainer.getStyleClass().add("top-container");
        menuItemsContainer.getStyleClass().add("menu-items-container");
        footerContainer.getStyleClass().add("footer-items-container");
        settingsContainer.getStyleClass().add("settings-container");
        hamburguerButton.getStyleClass().add("hamburger");
    }

    private PaneItemView createSettingsItem(MenuItem settingsMenuItem) {
        final PaneItemView settingsItem;
        settingsItem = new PaneItemViewLeaf(settingsMenuItem, shrunken.get());

        // Add listeners - we add listeners instead of binding so the icon can easily be overridden for example through CSS
        settingsMenuItem.textProperty().addListener(observable -> {
            settingsItem.setTitle(settingsMenuItem.getText());
        });
        settingsItem.setTitle(settingsMenuItem.getText());
        settingsMenuItem.graphicProperty().addListener(observable -> {
            settingsItem.setGraphic(settingsMenuItem.getGraphic());
        });


        settingsItem.setGraphic(settingsMenuItem.getGraphic());
        menuItemVisualRepresentation.put(settingsMenuItem, settingsItem);

        return settingsItem;
    }

    private void updateSettingsVisibility() {
        settingsContainer.setManaged(navigationPane.isSettingsVisible());
        settingsContainer.setVisible(navigationPane.isSettingsVisible());
        requestLayout();
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

    private void onItemsChanged(ListChangeListener.Change<? extends MenuItem> change, boolean isFooterItem) {
        VBox itemsContainer = isFooterItem ? footerContainer : menuItemsContainer;

        while(change.next()) {
            if (change.wasAdded()) {
                for (MenuItem addedMenuItem : change.getAddedSubList()) {
                    PaneItemView paneItemView = createItemRepresentation(addedMenuItem);
                    itemsContainer.getChildren().add(paneItemView.getNodeRepresentation());

                    addMenuItemListener(addedMenuItem);
                }
            }
            if (change.wasRemoved()) {
                for (MenuItem removedMenuItem : change.getRemoved()) {
                    Node nodeToRemove = menuItemVisualRepresentation.get(removedMenuItem).getNodeRepresentation();
                    itemsContainer.getChildren().remove((nodeToRemove));

                    menuItemsToActionListeners.remove(removedMenuItem);
                }
            }
        }
    }

    private void addMenuItemListener(MenuItem menuItem) {
        EventHandler<ActionEvent> actionEvent = event -> onMenuItemAction(event);
        menuItem.addEventHandler(ActionEvent.ACTION, actionEvent);
        menuItemsToActionListeners.put(menuItem, actionEvent);
    }

    private void onMenuItemAction(ActionEvent event) {
        MenuItem target = (MenuItem) event.getTarget();
        PaneItemView targetPaneItemView = menuItemVisualRepresentation.get(target);

        if (previouslySelectedMenuItem != null) {
            previouslySelectedMenuItem.setShowSelected(false);
        }

        if (targetPaneItemView.isShrunken() && targetPaneItemView.getParentItemView() != null) {
            PaneItemViewContainer paneItemViewContainer = targetPaneItemView.getParentItemView();
            paneItemViewContainer.setShowSelected(true);
            previouslySelectedMenuItem = paneItemViewContainer;
        } else {
            targetPaneItemView.setShowSelected(true);
            previouslySelectedMenuItem = targetPaneItemView;
        }

        selectedMenuItem.set(target);
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
            itemView = new PaneItemViewLeaf(menuItem, shrunken.get());
        }

        itemView.titleProperty().bind(menuItem.textProperty());
        itemView.graphicProperty().bind(menuItem.graphicProperty());

        menuItemVisualRepresentation.put(menuItem, itemView);

        return itemView;
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

        boolean settingsVisible = this.navigationPane.isSettingsVisible();

        double settingsContainerHeight = settingsVisible ? settingsContainer.prefHeight(availableWidth) : 0;
        double footerContainerHeight = footerContainer.prefHeight(availableWidth);

        // topContainer
        topContainer.resize(availableWidth, availableHeight - settingsContainerHeight - footerContainerHeight);

        // footer container
        double footerContainerY = availableHeight - settingsContainerHeight - footerContainerHeight;
        footerContainer.resizeRelocate(0, footerContainerY, availableWidth, footerContainerHeight);

        // settings container
        if (settingsVisible) {
            double settingsContainerY = availableHeight - settingsContainerHeight;
            settingsContainer.resizeRelocate(0, settingsContainerY, availableWidth, settingsContainerHeight);
        }
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
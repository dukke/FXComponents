package com.pixelduke.control;

import impl.com.pixelduke.control.NavigationPaneSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;

public class NavigationPane extends Control {
    private final ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    private final ObservableList<MenuItem> footerMenuItems = FXCollections.observableArrayList();

    private final ObjectProperty<MenuItem> selectedMenuItem = new SimpleObjectProperty<>();

    private BooleanProperty settingsVisible = new SimpleBooleanProperty();
    private BooleanProperty backButtonVisible = new SimpleBooleanProperty();

    private ObjectProperty<Node> content = new SimpleObjectProperty<>();

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NavigationPaneSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return NavigationPane.class.getResource("navigation-pane.css").toExternalForm();
    }

    /*=========================================================================*
     *                                                                         *
     *                         PROPERTIES                                      *
     *                                                                         *
     *=========================================================================*/

    // -- selected menu item
    public MenuItem getSelectedMenuItem() { return selectedMenuItem.get(); }
    public ObjectProperty<MenuItem> selectedMenuItemProperty() { return selectedMenuItem; }
    public void setSelectedMenuItem(MenuItem selectedMenuItem) { this.selectedMenuItem.set(selectedMenuItem); }

    // -- content
    public Node getContent() { return content.get(); }
    public ObjectProperty<Node> contentProperty() { return content; }
    public void setContent(Node content) { this.content.set(content); }

    // -- main menu items
    public ObservableList<MenuItem> getMenuItems() {return menuItems; }

    // -- footer menu items
    public ObservableList<MenuItem> getFooterMenuItems() { return footerMenuItems; }

    // -- settings visible
    public boolean isSettingsVisible() { return settingsVisible.get(); }
    public BooleanProperty settingsVisibleProperty() { return settingsVisible; }
    public void setSettingsVisible(boolean settingsVisible) { this.settingsVisible.set(settingsVisible); }

    // -- back button visible
    public boolean isBackButtonVisible() { return backButtonVisible.get(); }
    public BooleanProperty backButtonVisibleProperty() { return backButtonVisible; }
    public void setBackButtonVisible(boolean backButtonVisible) { this.backButtonVisible.set(backButtonVisible); }
}
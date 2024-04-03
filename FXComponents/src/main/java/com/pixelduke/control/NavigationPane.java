package com.pixelduke.control;

import impl.com.pixelduke.control.NavigationPaneSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NavigationPane extends Control {
    public static final String DEFAULT_SETTINGS_ICON_URL = NavigationPaneSkin.class.getResource("settings_icon.png").toExternalForm();

    private final MenuItem settingsMenuItem;
    private final ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    private final ObservableList<MenuItem> footerMenuItems = FXCollections.observableArrayList();

    private final ObjectProperty<MenuItem> selectedMenuItem = new SimpleObjectProperty<>();

    private BooleanProperty settingsVisible = new SimpleBooleanProperty();
    private BooleanProperty backButtonVisible = new SimpleBooleanProperty();

    private ObjectProperty<Node> content = new SimpleObjectProperty<>();

    private final DoubleProperty shrunkenWidth = new StyleableDoubleProperty(SHRUNKEN_WIDTH_DEFAULT_VALUE) {
        @Override
        public Object getBean() {
            return NavigationPane.this;
        }

        @Override
        public String getName() {
            return "shrunkenWidth";
        }

        @Override
        public CssMetaData<NavigationPane, Number> getCssMetaData() {
            return SHRUNKEN_WIDTH_META_DATA;
        }
    };

    private final DoubleProperty unshrunkenWidth = new StyleableDoubleProperty(UNSHRUNKEN_WIDTH_DEFAULT_VALUE) {
        @Override
        public Object getBean() {
            return NavigationPane.this;
        }

        @Override
        public String getName() {
            return "unshrunkenWidth";
        }

        @Override
        public CssMetaData<NavigationPane, Number> getCssMetaData() {
            return UNSHRUNKEN_WIDTH_META_DATA;
        }
    };

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
     *                        CONSTRUCTORS                                     *
     *                                                                         *
     *=========================================================================*/

    public NavigationPane() {
        this(new MenuItem("Settings", new ImageView(DEFAULT_SETTINGS_ICON_URL)));
    }

    public NavigationPane(MenuItem settingsMenuItem) {
        this.settingsMenuItem = settingsMenuItem;
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

    // -- shrunken width
    public double getShrunkenWidth() { return shrunkenWidth.get(); }
    public DoubleProperty shrunkenWidthProperty() { return shrunkenWidth; }
    public void setShrunkenWidth(double shrunkenWidth) { this.shrunkenWidth.set(shrunkenWidth); }

    // -- unshrunken width
    public double getUnshrunkenWidth() { return unshrunkenWidth.get(); }
    public DoubleProperty unshrunkenWidthProperty() { return unshrunkenWidth; }
    public void setUnshrunkenWidth(double unshrunkenWidth) { this.unshrunkenWidth.set(unshrunkenWidth); }

    // -- settings menu item
    public MenuItem getSettingsMenuItem() { return settingsMenuItem; }

    /**************************************************************************
     *                                                                        *
     *  CSS                                                                   *
     *                                                                        *
     *************************************************************************/

    private static final String SHRUNKEN_WIDTH_PROPERTY_NAME = "-shrunken-width";
    private static final String UNSHRUNKEN_WIDTH_PROPERTY_NAME = "-unshrunken-width";
    private static final double SHRUNKEN_WIDTH_DEFAULT_VALUE = 50;
    private static final double UNSHRUNKEN_WIDTH_DEFAULT_VALUE = 320;

    private static final CssMetaData<NavigationPane, Number> SHRUNKEN_WIDTH_META_DATA =
            new CssMetaData<>(SHRUNKEN_WIDTH_PROPERTY_NAME,
                              SizeConverter.getInstance(), SHRUNKEN_WIDTH_DEFAULT_VALUE) {

                @Override
                public boolean isSettable(NavigationPane navigationPane) {
                    return !navigationPane.shrunkenWidthProperty().isBound();
                }

                @Override
                public StyleableProperty<Number> getStyleableProperty(NavigationPane navigationPane) {
                    return (StyleableProperty<Number>) navigationPane.shrunkenWidthProperty();
                }
            };

    private static final CssMetaData<NavigationPane, Number> UNSHRUNKEN_WIDTH_META_DATA =
            new CssMetaData<>(UNSHRUNKEN_WIDTH_PROPERTY_NAME,
                              SizeConverter.getInstance(), UNSHRUNKEN_WIDTH_DEFAULT_VALUE) {

                @Override
                public boolean isSettable(NavigationPane navigationPane) {
                    return !navigationPane.unshrunkenWidthProperty().isBound();
                }

                @Override
                public StyleableProperty<Number> getStyleableProperty(NavigationPane navigationPane) {
                    return (StyleableProperty<Number>) navigationPane.unshrunkenWidthProperty();
                }
            };

    private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    static {
        final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(SkinBase.getClassCssMetaData());
        styleables.add(SHRUNKEN_WIDTH_META_DATA);
        styleables.add(UNSHRUNKEN_WIDTH_META_DATA);
        STYLEABLES = Collections.unmodifiableList(styleables);
    }

    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return STYLEABLES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
}
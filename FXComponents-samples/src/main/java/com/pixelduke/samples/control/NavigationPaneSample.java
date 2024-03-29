package com.pixelduke.samples.control;

import com.pixelduke.control.NavigationPane;
import com.pixelduke.transit.Style;
import com.pixelduke.transit.TransitTheme;
import com.pixelduke.window.ThemeWindowManagerFactory;
import com.pixelduke.window.Win11ThemeWindowManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NavigationPaneSample  extends Application {
    public static void main(String[] args) {
        System.setProperty("prism.forceUploadingPainter", "true");
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        System.setProperty("prism.lcdtext", "false");

        NavigationPane navigationPane = new NavigationPane();

        // menu items
        ImageView menuItem1Graphic = new ImageView(NavigationPaneSample.class.getResource("icons8-home-20.png").toExternalForm());
        navigationPane.getMenuItems().add(new MenuItem("Home", menuItem1Graphic));
        ImageView menuItem2Graphic = new ImageView(NavigationPaneSample.class.getResource("icons8-design-20.png").toExternalForm());
        navigationPane.getMenuItems().add(new MenuItem("Design guidance", menuItem2Graphic));
        ImageView menuItem3Graphic = new ImageView(NavigationPaneSample.class.getResource("icons8-list-20.png").toExternalForm());
        navigationPane.getMenuItems().add(new MenuItem("All samples", menuItem3Graphic));
        // -- menu
        ImageView menuItem4Graphic = new ImageView(NavigationPaneSample.class.getResource("icons8-document-20.png").toExternalForm());
        Menu menu = new Menu("Document actions", menuItem4Graphic);
        ImageView menuItem5Graphic = new ImageView(NavigationPaneSample.class.getResource("icons8-rescan-document-20.png").toExternalForm());
        menu.getItems().add(new MenuItem("Rescan document", menuItem5Graphic));
        ImageView menuItem6Graphic = new ImageView(NavigationPaneSample.class.getResource("icons8-insert-page-20.png").toExternalForm());
        menu.getItems().add(new MenuItem("Add page", menuItem6Graphic));
        navigationPane.getMenuItems().add(menu);

        // footer menu items
        ImageView footerMenuItem1 = new ImageView(NavigationPaneSample.class.getResource("icons8-account-20.png").toExternalForm());
        navigationPane.getFooterMenuItems().add(new MenuItem("Account", footerMenuItem1));
        ImageView footerMenuItem2 = new ImageView(NavigationPaneSample.class.getResource("icons8-help-20.png").toExternalForm());
        navigationPane.getFooterMenuItems().add(new MenuItem("Help", footerMenuItem2));

        navigationPane.setContent(new SampleUI());

        Scene scene = new Scene(navigationPane, 1250, 630);
        scene.setFill(Color.TRANSPARENT);

        stage.setTitle("NavigationPane Sample");
        stage.initStyle(StageStyle.UNIFIED);
        stage.getIcons().add(new Image(NavigationPaneSample.class.getResourceAsStream("fxcomponents_icon.jpg")));

        new TransitTheme(scene, Style.LIGHT);

        stage.setScene(scene);
        stage.show();

        Win11ThemeWindowManager win11ThemeWindowManager = (Win11ThemeWindowManager) ThemeWindowManagerFactory.create();
        win11ThemeWindowManager.setWindowBackdrop(stage, Win11ThemeWindowManager.Backdrop.MICA_ALT);
    }
}
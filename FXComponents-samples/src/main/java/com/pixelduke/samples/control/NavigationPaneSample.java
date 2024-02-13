package com.pixelduke.samples.control;

import com.pixelduke.control.NavigationPane;
import com.pixelduke.transit.Style;
import com.pixelduke.transit.TransitTheme;
import com.pixelduke.window.ThemeWindowManagerFactory;
import com.pixelduke.window.Win11ThemeWindowManager;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
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

        ImageView menuItem1Graphic = new ImageView(NavigationPaneSample.class.getResource("icons8-home-20.png").toExternalForm());
        navigationPane.getMenuItems().add(new MenuItem("Home", menuItem1Graphic));
        ImageView menuItem2Graphic = new ImageView(NavigationPaneSample.class.getResource("icons8-design-20.png").toExternalForm());
        navigationPane.getMenuItems().add(new MenuItem("Design guidance", menuItem2Graphic));
        ImageView menuItem3Graphic = new ImageView(NavigationPaneSample.class.getResource("icons8-list-20.png").toExternalForm());
        navigationPane.getMenuItems().add(new MenuItem("All samples", menuItem3Graphic));

//        ImageContainer imageContainer = new ImageContainer();
//        imageContainer.setImage(new Image(NavigationPaneSample.class.getResource("sample_for_navigation_pane.png").toExternalForm()));
//        navigationPane.setContent(imageContainer);

        Scene scene = new Scene(navigationPane, 1000, 600);
        scene.setFill(Color.TRANSPARENT);

        stage.setTitle("NavigationPane Sample");
        stage.initStyle(StageStyle.UNIFIED);
        stage.getIcons().add(new Image(NavigationPaneSample.class.getResourceAsStream("fxcomponents_icon.jpg")));

        new TransitTheme(scene, Style.LIGHT);

        stage.setScene(scene);
        stage.show();

        Win11ThemeWindowManager win11ThemeWindowManager = (Win11ThemeWindowManager) ThemeWindowManagerFactory.create();
        win11ThemeWindowManager.setWindowBackdrop(stage, Win11ThemeWindowManager.Backdrop.MICA);
    }

    class ImageContainer extends Region {
        private final ImageView imageView = new ImageView();

        public ImageContainer() {
            getChildren().add(imageView);
        }

        @Override
        protected void layoutChildren() {
            double leftPadding = snappedLeftInset();
            double topPadding = snappedTopInset();
            double rightPadding = snappedRightInset();
            double bottomPadding = snappedBottomInset();

            double width = getWidth();
            double height = getHeight();

            double imageViewAvailableWidth = width - leftPadding - rightPadding;
            double imageViewAvailableHeight = height - topPadding - bottomPadding;

            imageView.relocate(leftPadding, topPadding);
            imageView.setFitWidth(imageViewAvailableWidth);
            imageView.setFitHeight(imageViewAvailableHeight);
        }

        // -- image
        public void setImage(Image image) { imageView.setImage(image); }
        public Image getImage() { return imageView.getImage(); }
        public ObjectProperty<Image> imageProperty() { return imageView.imageProperty(); }
    }
}
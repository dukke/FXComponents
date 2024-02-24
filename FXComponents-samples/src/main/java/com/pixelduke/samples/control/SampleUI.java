package com.pixelduke.samples.control;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SampleUI extends Region {
    private BorderPane mainContainer = new BorderPane();

    public SampleUI() {

        Label titleHeader = new Label("Activities");
        mainContainer.setTop(titleHeader);

        /*******************************  Top Part  ***********************************/
        BorderPane topContainer = new BorderPane();
        VBox contentContainer = new VBox();

        ImageContainer imageContainer = new ImageContainer();
        imageContainer.setImage(new Image(SampleUI.class.getResourceAsStream("pexels-tom-fisk(300x300).jpg")));
        imageContainer.setPrefWidth(300);
        imageContainer.setPrefHeight(300);

        topContainer.setLeft(imageContainer);

        VBox rightTextContainer = new VBox();
        Label rightTextHeader = new Label("Things to do and follow");
        Label rightText = new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna " +
                                             "aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
                                             " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint " +
                                             "occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        Label rightText2 = new Label("Vestibulum lectus mauris ultrices eros. Quis commodo odio aenean sed adipiscing diam. Viverra aliquet eget sit amet. " +
                                             "Facilisis magna etiam tempor orci eu lobortis elementum nibh tellus. Sagittis id consectetur purus ut faucibus. Ultrices " +
                                             "vitae auctor eu augue ut lectus arcu bibendum at. Augue mauris augue neque gravida in. Amet consectetur adipiscing elit ut " +
                                             "aliquam purus sit amet.");
        rightText.setMaxWidth(500);
        rightTextContainer.getChildren().addAll(rightTextHeader, rightText, rightText2);

        topContainer.setCenter(rightTextContainer);

        contentContainer.getChildren().add(topContainer);

        /*****************************  Bottom Part  ***********************************/

        Label bottomHeading = new Label("Points of interest");
        contentContainer.getChildren().add(bottomHeading);

        HBox bottomPicsContainer = new HBox();

        var imageSize = 160;
        bottomPicsContainer.getChildren().addAll(
                createPictureWithCaption("pexels-davi-pimentel(160px).jpg", "Colosseum", imageSize),
                createPictureWithCaption("pexels-jess-loiterton(160px).jpg", "Beach", imageSize),
                createPictureWithCaption("pexels-jill-burrow(160px).jpg", "Breakfast", imageSize),
                createPictureWithCaption("pexels-jill-burrow-6858665(160px).jpg", "Lakes", imageSize),
                createPictureWithCaption("pexels-namzy(160px).jpg", "Coffee shops", imageSize)
        );
        contentContainer.getChildren().add(bottomPicsContainer);

        mainContainer.setCenter(contentContainer);

        getChildren().add(mainContainer);

        // CSS
        getStyleClass().add("sample-ui");

        titleHeader.getStyleClass().add("title-header");
        rightTextHeader.getStyleClass().add("right-text-header");
        rightText.getStyleClass().add("right-text");
        rightText2.getStyleClass().add("right-text-2");
        rightTextContainer.getStyleClass().add("right-text-container");
        bottomHeading.getStyleClass().add("bottom-heading");

        topContainer.getStyleClass().add("top-container");
        bottomPicsContainer.getStyleClass().add("bottom-pics-container");

        getStylesheets().add(SampleUI.class.getResource("sample-ui.css").toExternalForm());
    }

    private VBox createPictureWithCaption(String fileName, String caption, double size) {
        VBox pictureContainer = new VBox();

        ImageContainer imageContainer = new ImageContainer();
        Image image = new Image(SampleUI.class.getResourceAsStream(fileName));
        imageContainer.setImage(image);

        Label captionLabel = new Label(caption);

        pictureContainer.getChildren().addAll(imageContainer, captionLabel);

        pictureContainer.setMaxWidth(size);
        pictureContainer.setMaxHeight(size);

        // CSS
        pictureContainer.getStyleClass().add("picture-container");

        return pictureContainer;
    }

    private class ImageContainer extends Region {
        private final ImageView imageView = new ImageView();

        public ImageContainer() {
            imageView.setSmooth(true);

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

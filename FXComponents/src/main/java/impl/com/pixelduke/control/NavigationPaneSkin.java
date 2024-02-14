package impl.com.pixelduke.control;

import com.pixelduke.control.NavigationPane;
import javafx.beans.binding.Bindings;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class NavigationPaneSkin extends SkinBase<NavigationPane> {
    private static final String BACK_ICON_URL = NavigationPaneSkin.class.getResource("back_icon.png").toExternalForm();

    private final BorderPane mainContainer = new BorderPane();
    private final HBox topContainer = new HBox();
    private final StackPane contentContainer = new StackPane();
    private final NavigationPaneLeftPane leftContainer = new NavigationPaneLeftPane();

    public NavigationPaneSkin(NavigationPane navigationPane) {
        super(navigationPane);

        mainContainer.setLeft(leftContainer);
        mainContainer.setTop(topContainer);
        mainContainer.setCenter(contentContainer);
        getChildren().add(mainContainer);

//        ImageView backButtonImageView = new ImageView(BACK_ICON_URL);
//        Button backButton = new Button();
//        backButton.getStyleClass().add("light");
//        backButton.setGraphic(backButtonImageView);
//        backButton.setFocusTraversable(false);
//        topContainer.getChildren().add(backButton);

        Bindings.bindContent(leftContainer.getMenuItems(), navigationPane.getMenuItems());
        Bindings.bindContent(leftContainer.getFooterMenuItems(), navigationPane.getFooterMenuItems());

        if (navigationPane.getContent() != null) {
            contentContainer.getChildren().setAll(navigationPane.getContent());
        }
        navigationPane.contentProperty().addListener(observable -> contentContainer.getChildren().setAll(navigationPane.getContent()));

        // CSS
        navigationPane.getStyleClass().setAll("navigation-pane");
        mainContainer.getStyleClass().add("main-container");
        contentContainer.getStyleClass().add("content");
        topContainer.getStyleClass().add("top-container");
    }
}
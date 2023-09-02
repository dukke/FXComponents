package com.pixelduke.control;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * A blocking dialog (blocks user input) that shows a progress bar while a background operation is in progress.
 * The ProgressBar can be of indeterminate progress or not.
 * The developer passes a Runnable to the showAndWait method. That Runnable will be executed in a background task, progress
 * can be updated through convenience methods in the Task class API.
 */
public class BlockingProgressBar {

    /**
     * Value for progress indicating that the progress is indeterminate.
     */
    public static final double INDETERMINATE_PROGRESS = ProgressBar.INDETERMINATE_PROGRESS;

    /*=========================================================================*
     *                                                                         *
     *                        INSTANCE FIELDS                                  *
     *                                                                         *
     *=========================================================================*/

    private final FlatDialog<?> dialog = new FlatDialog<>();
    private final Label contentLabel = new Label();
    private final ProgressBar progressBar = new ProgressBar();
    private final VBox contentContainer = new VBox();

    private final ObservableList<String> styleClasses = FXCollections.observableArrayList();

    private Task<?> task;

    /*=========================================================================*
     *                                                                         *
     *                        CONSTRUCTORS                                     *
     *                                                                         *
     *=========================================================================*/

    public BlockingProgressBar(String headerText) {
        this(headerText, null, ProgressBar.INDETERMINATE_PROGRESS);
    }

    public BlockingProgressBar(String headerText, String contentText) {
        this(headerText, contentText, ProgressBar.INDETERMINATE_PROGRESS);
    }

    public BlockingProgressBar(String title, String contentText, double progress) {
        progressBar.setMaxWidth(Double.MAX_VALUE);

        contentLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (contentContainer.getChildren().isEmpty() || contentContainer.getChildren().get(0) != contentLabel) {
                    contentContainer.getChildren().setAll(contentLabel, progressBar);
                }
            } else {
                if (contentContainer.getChildren().isEmpty() || contentContainer.getChildren().get(0) != progressBar) {
                    contentContainer.getChildren().setAll(progressBar);
                }
            }

            if (dialog.getDialogPane().getContent() != contentContainer) {
                dialog.getDialogPane().setContent(contentContainer);
            }
        });

        contentContainer.setSpacing(10);

        setHeaderText(title);
        setMessage(contentText);
        progressBar.setProgress(progress);

        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> {
            if (task != null) {
                task.cancel();
            }
        });

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setManaged(false);
        closeButton.setVisible(false);

        styleClasses.addListener(this::onStyleClassesChanged);

        // CSS
        dialog.getDialogPane().getStyleClass().add(DEFAULT_STYLE_CLASS);
        contentContainer.getStyleClass().add("content-container");
    }

    /*=========================================================================*
     *                                                                         *
     *                         PROPERTIES                                      *
     *                                                                         *
     *=========================================================================*/

    // --- Showing
    /**
     * Represents whether the BlockingProgressBar instance is currently showing or not.
     * @return a {@link ReadOnlyBooleanProperty}
     */
    public final ReadOnlyBooleanProperty showingProperty() {
        return dialog.showingProperty();
    }
    public final boolean isShowing() { return dialog.isShowing(); }

    // --- Title
    public final StringProperty headerTextProperty() { return dialog.headerTextProperty(); }
    public final void setHeaderText(String text) { dialog.setHeaderText(text); }
    public final String getHeaderText() { return dialog.getHeaderText(); }

    // --- Message
    public final StringProperty messageProperty() { return contentLabel.textProperty(); }
    public final void setMessage(String text) { contentLabel.setText(text); }
    public final String getMessage() { return contentLabel.getText(); }

    // --- StyleClasses
    public ObservableList<String> getStyleClass() { return styleClasses; }

    /**
     * The id of this {@code BlockingProgressBar}. This simple string identifier is useful for
     * finding a specific BlockingProgressBar within the scene graph. While the id of a BlockingProgressBar
     * should be unique within the scene graph, this uniqueness is not enforced.
     * This is analogous to the "id" attribute on an HTML element
     * (<a href="http://www.w3.org/TR/CSS21/syndata.html#value-def-identifier">CSS ID Specification</a>).
     * <p>
     *     For example, if a BlockingProgressBar is given the id of "myId", then the lookup method can
     *     be used to find this dialog as follows: <code>scene.lookup("#myId");</code>.
     * </p>
     *
     * @defaultValue null
     */
    private final StringProperty id = new SimpleStringProperty();
    public final StringProperty idProperty() { return id; }
    public final void setId(String id) { this.id.setValue(id); }
    public final String getId() { return this.id.get(); }



//    // - Indeterminate
//    public final boolean isIndeterminate() {
//        return progressBar.isIndeterminate();
//    }
//    public final ReadOnlyBooleanProperty indeterminateProperty() {
//        return progressBar.indeterminateProperty();
//    }
//
//    // - Progress
//    public final void setProgress(double value) {
//        progressBar.setProgress(value);
//    }
//    public final double getProgress() {
//        return progressBar.getProgress();
//    }
//    public final DoubleProperty progressProperty() {
//        return progressBar.progressProperty();
//    }

    /*=========================================================================*
     *                                                                         *
     *                         EVENTS                                          *
     *                                                                         *
     *=========================================================================*/

    /**
     * Called just prior to the Dialog being shown.
     */
    public final void setOnShowing(EventHandler<DialogEvent> value) { dialog.setOnShowing(event -> Platform.runLater(() -> value.handle(event))); }
    public final EventHandler<DialogEvent> getOnShowing() {
        return dialog.getOnShowing();
    }
    public final ObjectProperty<EventHandler<DialogEvent>> onShowingProperty() { return dialog.onShowingProperty(); }

    /**
     * Called just after the Dialog is shown.
     */
    public final void setOnShown(EventHandler<DialogEvent> value) { dialog.setOnShown(event -> Platform.runLater(() -> value.handle(event))); }
    public final EventHandler<DialogEvent> getOnShown() { return dialog.getOnShown(); }
    public final ObjectProperty<EventHandler<DialogEvent>> onShownProperty() { return dialog.onShownProperty(); }

    /**
     * Called just prior to the Dialog being hidden.
     */
    public final void setOnHiding(EventHandler<DialogEvent> value) { dialog.setOnHiding(event -> Platform.runLater(() -> value.handle(event))); }
    public final EventHandler<DialogEvent> getOnHiding() { return dialog.getOnHiding(); }
    public final ObjectProperty<EventHandler<DialogEvent>> onHidingProperty() { return dialog.onHidingProperty(); }

    /**
     * Called just after the Dialog has been hidden.
     * When the {@code Dialog} is hidden, this event handler is invoked allowing
     * the developer to clean up resources or perform other tasks when the
     * {@link Alert} is closed.
     */
    public final void setOnHidden(EventHandler<DialogEvent> value) { dialog.setOnHidden(event -> Platform.runLater(() -> value.handle(event))); }
    public final EventHandler<DialogEvent> getOnHidden() { return dialog.getOnHidden(); }
    public final ObjectProperty<EventHandler<DialogEvent>> onHiddenProperty() { return dialog.onHiddenProperty(); }

    /*=========================================================================*
     *                                                                         *
     *                        PUBLIC API                                       *
     *                                                                         *
     *=========================================================================*/

    public final <V> void showAndWait(Task<V> runOnBackground) {
//        task = new Task() {
//            @Override
//            protected Object call() throws Exception {
//                runOnBackground.run();
//                Platform.runLater(() -> hide());
//                return null;
//            }
//        };
        task = runOnBackground;

        task.stateProperty().addListener(observable -> {
            if (task.getState() == Worker.State.CANCELLED
                  || task.getState() == Worker.State.FAILED
                  || task.getState() == Worker.State.SUCCEEDED) {
                Platform.runLater(this::hide);

                if (task.getState() == Worker.State.FAILED) {
                    // If the task fails we rethrow the exception, otherwise it will get eaten and nothing will show
                    // in the app using this library (no exception information will show)
                    throw new RuntimeException(task.getException());
                }
            }
        });

        progressBar.progressProperty().bind(task.progressProperty());
        messageProperty().bind(task.messageProperty());

        Thread thread = new Thread(runOnBackground);
        thread.setDaemon(true);
        thread.start();

        showAndWait();
    }

    /**
     * Hides/closes the BlockingProgressBar.
     */
    public final void hide() {
        dialog.hide();
    }

    public void initOwner(Stage owner) {
        dialog.initOwner(owner);
    }

    /*=========================================================================*
     *                                                                         *
     *                        PRIVATE API                                      *
     *                                                                         *
     *=========================================================================*/

    private void showAndWait() {
        dialog.showAndWait();
    }

    private void onStyleClassesChanged(ListChangeListener.Change<? extends String> changed) {
        while (changed.next()) {
            if (changed.wasAdded()) {
                dialog.getDialogPane().getStyleClass().addAll(changed.getAddedSubList());
            }
            if (changed.wasRemoved()) {
                dialog.getDialogPane().getStyleClass().removeAll(changed.getRemoved());
            }
        }
    }

    /*=========================================================================*
     *                                                                         *
     *                      STYLESHEET HANDLING                                *
     *                                                                         *
     *=========================================================================*/

    private static final String DEFAULT_STYLE_CLASS = "blocking-progress-bar";
}


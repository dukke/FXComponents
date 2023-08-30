package com.pixelduke.control;

import impl.java.pixelduke.control.ListBuilderSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A control with 2 lists. A source list and a target list.
 * The target list will contain all the elemenns the user chose from the source list.
 * The user can drag and drop items from the source list onto the target list or use the buttons available to accomplish that.
 *
 * @param <T> The type of the items that will be present in the source and target list
 */
public class ListBuilder<T> extends Control {
    private static final String DEFAULT_STYLECLASS = "list-builder";

    private static <T> void moveToTarget(ListView<T> sourceListView, ListView<T> targetListView) {
        move(sourceListView, targetListView);
        sourceListView.getSelectionModel().clearSelection();
    }

    private static <T> void moveToTargetAll(ListView<T> sourceListView, ListView<T> targetListView) {
        move(sourceListView, targetListView, new ArrayList<>(sourceListView.getItems()));
        sourceListView.getSelectionModel().clearSelection();
    }

    private static <T> void moveToSource(ListView<T> sourceListView, ListView<T> targetListView) {
        move(targetListView, sourceListView);
        targetListView.getSelectionModel().clearSelection();
    }

    private static <T> void moveToSourceAll(ListView<T> sourceListView, ListView<T> targetListView) {
        move(targetListView, sourceListView, new ArrayList<>(targetListView.getItems()));
        targetListView.getSelectionModel().clearSelection();
    }

    private static <T> void move(ListView<T> source, ListView<T> target) {
        List<T> selectedItems = new ArrayList<>(source.getSelectionModel().getSelectedItems());
        move(source, target, selectedItems);
    }

    private static <T> void move(ListView<T> source, ListView<T> target, List<T> items) {
        source.getItems().removeAll(items);
        target.getItems().addAll(items);
    }

    /*=========================================================================*
     *                                                                         *
     *                        INSTANCE FIELDS                                  *
     *                                                                         *
     *=========================================================================*/

    private final ObjectProperty<Node> sourceHeader = new SimpleObjectProperty<>(this, "sourceHeader");
    private final ObjectProperty<Node> targetHeader = new SimpleObjectProperty<>(this, "targetHeader");

    private ObjectProperty<ObservableList<T>> sourceItems;
    private ObjectProperty<ObservableList<T>> targetItems;

    private ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory;

    /**
     * This is the item that will be automatically added to the list by the ListBuilder implementation when something
     * is dragged over it.
     */
    private final T placeHolderItem;

    /*=========================================================================*
     *                                                                         *
     *                        CONSTRUCTORS                                     *
     *                                                                         *
     *=========================================================================*/

    /**
     * Constructs a new dual list view.
     */
    public ListBuilder(T placeholderItem) {
        getStyleClass().add(DEFAULT_STYLECLASS);

        this.placeHolderItem = placeholderItem;

        Label sourceHeader = new Label("");
        sourceHeader.getStyleClass().add("list-header-label");
        setSourceHeader(sourceHeader);

        Label targetHeader = new Label("");
        targetHeader.getStyleClass().add("list-header-label");
        setTargetHeader(targetHeader);
    }

    @Override
    protected Skin<ListBuilder<T>> createDefaultSkin() {
        return new ListBuilderSkin<>(this);
    }

    /** {@inheritDoc} */
    @Override public String getUserAgentStylesheet() {
        return Objects.requireNonNull(ListBuilder.class.getResource("list-builder.css")).toExternalForm();
    }

    /*=========================================================================*
     *                                                                         *
     *                        PUBLIC API                                       *
     *                                                                         *
     *=========================================================================*/

    public T getPlaceHolderItem() {
        return placeHolderItem;
    }


    /*=========================================================================*
     *                                                                         *
     *                         PROPERTIES                                      *
     *                                                                         *
     *=========================================================================*/

    // --- source header

    /**
     * A property used to store a reference to a node that will be displayed
     * above the source list view. The default node is a {@link Label}
     * displaying the text "Available".
     *
     * @return the property used to store the source header node
     */
    public final ObjectProperty<Node> sourceHeaderProperty() {
        return sourceHeader;
    }

    /**
     * Returns the value of {@link #sourceHeaderProperty()}.
     *
     * @return the source header node
     */
    public final Node getSourceHeader() {
        return sourceHeader.get();
    }

    /**
     * Sets the value of {@link #sourceHeaderProperty()}.
     *
     * @param node
     *            the new header node to use for the source list
     */
    public final void setSourceHeader(Node node) {
        sourceHeader.set(node);
    }

    // --- target header

    /**
     * A property used to store a reference to a node that will be displayed
     * above the target list view. The default node is a {@link Label}
     * displaying the text "Selected".
     *
     * @return the property used to store the target header node
     */
    public final ObjectProperty<Node> targetHeaderProperty() {
        return targetHeader;
    }

    /**
     * Returns the value of {@link #targetHeaderProperty()}.
     *
     * @return the source header node
     */
    public final Node getTargetHeader() {
        return targetHeader.get();
    }

    /**
     * Sets the value of {@link #targetHeaderProperty()}.
     *
     * @param node
     *            the new node shown above the target list
     */
    public final void setTargetHeader(Node node) {
        targetHeader.set(node);
    }

    // --- source items

    /**
     * Sets the underlying data model for the ListView. Note that it has a
     * generic type that must match the type of the ListView itself.
     */
    public final void setSourceItems(ObservableList<T> value) {
        sourceItemsProperty().set(value);
    }

    /**
     * Returns an {@link ObservableList} that contains the items currently being
     * shown to the user in the source list. This may be null if
     * {@link #setSourceItems(javafx.collections.ObservableList)} has previously
     * been called, however, by default it is an empty ObservableList.
     *
     * @return An ObservableList containing the items to be shown to the user in
     *         the source list, or null if the items have previously been set to
     *         null.
     */
    public final ObservableList<T> getSourceItems() {
        return sourceItemsProperty().get();
    }

    /**
     * The underlying data model for the source list view. Note that it has a
     * generic type that must match the type of the source list view itself.
     */
    public final ObjectProperty<ObservableList<T>> sourceItemsProperty() {
        if (sourceItems == null) {
            sourceItems = new SimpleObjectProperty<>(this, "sourceItems",
                    FXCollections.observableArrayList());
        }
        return sourceItems;
    }

    // --- target items

    /**
     * Sets the underlying data model for the ListView. Note that it has a
     * generic type that must match the type of the ListView itself.
     */
    public final void setTargetItems(ObservableList<T> value) {
        targetItemsProperty().set(value);
    }

    /**
     * Returns an {@link ObservableList} that contains the items currently being
     * shown to the user in the target list. This may be null if
     * {@link #setTargetItems(javafx.collections.ObservableList)} has previously
     * been called, however, by default it is an empty ObservableList.
     *
     * @return An ObservableList containing the items to be shown to the user in
     *         the target list, or null if the items have previously been set to
     *         null.
     */
    public final ObservableList<T> getTargetItems() {
        return targetItemsProperty().get();
    }

    /**
     * The underlying data model for the target list view. Note that it has a
     * generic type that must match the type of the source list view itself.
     */
    public final ObjectProperty<ObservableList<T>> targetItemsProperty() {
        if (targetItems == null) {
            targetItems = new SimpleObjectProperty<>(this, "targetItems",
                    FXCollections.observableArrayList());
        }
        return targetItems;
    }

    // --- cell factory

    /**
     * Sets a new cell factory to use by both list views. This forces all old
     * {@link ListCell}'s to be thrown away, and new ListCell's created with the
     * new cell factory.
     */
    public final void setCellFactory(Callback<ListView<T>, ListCell<T>> value) {
        cellFactoryProperty().set(value);
    }

    /**
     * Returns the current cell factory.
     */
    public final Callback<ListView<T>, ListCell<T>> getCellFactory() {
        return cellFactory == null ? null : cellFactory.get();
    }

    /**
     * <p>
     * Setting a custom cell factory has the effect of deferring all cell
     * creation, allowing for total customization of the cell. Internally, the
     * ListView is responsible for reusing ListCells - all that is necessary is
     * for the custom cell factory to return from this function a ListCell which
     * might be usable for representing any item in the ListView.
     *
     * <p>
     * Refer to the {@link Cell} class documentation for more detail.
     */
    public final ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() {
        if (cellFactory == null) {
            cellFactory = new SimpleObjectProperty<>(this, "cellFactory");
        }
        return cellFactory;
    }


}

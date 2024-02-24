module com.pixelduke.samples.fxcomponents {
    requires com.pixelduke.fxcomponents;
    requires com.pixelduke.transit;
    requires com.pixelduke.fxthemes;

    exports com.pixelduke.samples.control to javafx.graphics;

    opens com.pixelduke.samples.control to javafx.base;
}
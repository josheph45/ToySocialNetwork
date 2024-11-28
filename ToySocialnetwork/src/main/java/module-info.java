module app.toysocialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    exports app.toysocialnetwork.gui;
    opens app.toysocialnetwork.gui to javafx.fxml;
}
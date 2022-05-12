module com.example.athensfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.athensfx to javafx.fxml;
    exports com.example.athensfx;
}
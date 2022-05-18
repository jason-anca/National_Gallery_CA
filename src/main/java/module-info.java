module com.example.national_gallery_ca {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.national_gallery_ca to javafx.fxml;
    exports com.example.national_gallery_ca;
}
module com.example.g {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.g2048 to javafx.fxml;
    exports com.example.g2048;
}
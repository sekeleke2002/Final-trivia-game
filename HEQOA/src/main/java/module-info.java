module com.example.heqoa {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.heqoa to javafx.fxml;
    exports com.example.heqoa;
}
module com.example.ootrabalhopratico {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ootrabalhopratico to javafx.fxml;
    exports com.example.ootrabalhopratico;
}
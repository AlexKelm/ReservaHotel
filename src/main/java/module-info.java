module com.alexkelm.reservahotel {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.alexkelm.reservahotel to javafx.fxml;
    exports com.alexkelm.reservahotel;
}
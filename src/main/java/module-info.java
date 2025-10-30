module com.alexkelm.reservahotel {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.alexkelm.reservahotel to javafx.fxml;
    exports com.alexkelm.reservahotel;

    opens com.alexkelm.reservahotel.dao to javafx.fxml;
    exports com.alexkelm.reservahotel.dao;

    opens com.alexkelm.reservahotel.models to javafx.fxml;
    exports com.alexkelm.reservahotel.models;

    opens com.alexkelm.reservahotel.controllers to javafx.fxml;
    exports com.alexkelm.reservahotel.controllers;

}
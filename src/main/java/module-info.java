module com.segovia.reservahotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.segovia.reservahotel to javafx.fxml;
    exports com.segovia.reservahotel;

    opens com.segovia.reservahotel.models to javafx.fxml;
    exports com.segovia.reservahotel.models;

    opens com.segovia.reservahotel.dao to javafx.fxml;
    exports com.segovia.reservahotel.dao;

    opens com.segovia.reservahotel.controllers to javafx.fxml;
    exports com.segovia.reservahotel.controllers;
}
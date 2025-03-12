module org.example.autopark_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens org.example.autopark_client to javafx.fxml;
    exports org.example.autopark_client;

    opens org.example.autopark_client.controller to javafx.fxml;
    exports org.example.autopark_client.controller;

    opens org.example.autopark_client.service to javafx.fxml;
    exports org.example.autopark_client.service;

    opens org.example.autopark_client.dto to javafx.fxml;
    exports org.example.autopark_client.dto;
}

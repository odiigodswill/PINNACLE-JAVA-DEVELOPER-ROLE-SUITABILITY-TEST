module com.pinnacle.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
//    requires eu.hansolo.tilesfx;
    requires static lombok;
    requires spring.web;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;

    exports com.pinnacle.frontend;
    exports com.pinnacle.frontend.controller;
    exports com.pinnacle.frontend.model;
    opens com.pinnacle.frontend.model to com.fasterxml.jackson.databind;
    opens com.pinnacle.frontend.controller to javafx.fxml;
//    exports com.pinnacle.frontend;
}
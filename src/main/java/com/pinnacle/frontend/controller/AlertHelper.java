package com.pinnacle.frontend.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertHelper {
    public static boolean confirm(String title, String body) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, body, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        var res = alert.showAndWait();
        return res.isPresent() && res.get() == ButtonType.YES;
    }
}

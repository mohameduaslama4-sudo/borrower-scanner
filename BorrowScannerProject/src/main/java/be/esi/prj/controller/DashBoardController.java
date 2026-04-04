package be.esi.prj.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import be.esi.prj.util.*;
import javafx.stage.Stage;

public class DashBoardController {

    @FXML
    private Button inventoryButton;
    @FXML
    private Button usersButton;
    @FXML
    private Button historyButton;

    @FXML
    public void initialize() {
        inventoryButton.setOnAction(e -> SceneManager.switchScene((Stage) inventoryButton.getScene().getWindow(), "InventoryView.fxml"));
        usersButton.setOnAction(e -> SceneManager.switchScene((Stage) usersButton.getScene().getWindow(), "UsersView.fxml"));
        historyButton.setOnAction(e -> SceneManager.switchScene((Stage) historyButton.getScene().getWindow(), "HistoryView.fxml"));
    }
}

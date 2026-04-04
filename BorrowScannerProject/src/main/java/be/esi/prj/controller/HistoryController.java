package be.esi.prj.controller;

import be.esi.prj.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HistoryController {

    @FXML
    private Button dashboardButton;
    @FXML
    private Button inventoryButton;
    @FXML
    private Button usersButton;

    @FXML
    public void initialize() {
        dashboardButton.setOnAction(e -> SceneManager.switchScene((Stage) dashboardButton.getScene().getWindow(), "DashBoardView.fxml"));
        inventoryButton.setOnAction(e -> SceneManager.switchScene((Stage) inventoryButton.getScene().getWindow(), "InventoryView.fxml"));
        usersButton.setOnAction(e -> SceneManager.switchScene((Stage) usersButton.getScene().getWindow(), "UsersView.fxml"));
    }



}

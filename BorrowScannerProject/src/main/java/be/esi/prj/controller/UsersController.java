package be.esi.prj.controller;

import be.esi.prj.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class UsersController {

    @FXML
    private Button dashboardButton;
    @FXML
    private Button inventoryButton;
    @FXML
    private Button historyButton;

    @FXML
    public void initialize() {
        dashboardButton.setOnAction(e -> SceneManager.switchScene((Stage) dashboardButton.getScene().getWindow(), "DashBoardView.fxml"));
        inventoryButton.setOnAction(e -> SceneManager.switchScene((Stage) inventoryButton.getScene().getWindow(), "InventoryView.fxml"));
        historyButton.setOnAction(e -> SceneManager.switchScene((Stage) historyButton.getScene().getWindow(), "HistoryView.fxml"));
    }

}

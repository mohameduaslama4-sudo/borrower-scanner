package be.esi.prj.controller;

import be.esi.prj.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InventoryController {

    @FXML
    private Button dashboardButton;
    @FXML
    private Button usersButton;
    @FXML
    private Button historyButton;

    @FXML
    public void initialize() {
        dashboardButton.setOnAction(e -> SceneManager.switchScene((Stage) dashboardButton.getScene().getWindow(), "DashBoardView.fxml"));
        usersButton.setOnAction(e -> SceneManager.switchScene((Stage) usersButton.getScene().getWindow(), "UsersView.fxml"));
        historyButton.setOnAction(e -> SceneManager.switchScene((Stage) historyButton.getScene().getWindow(), "HistoryView.fxml"));
    }

}

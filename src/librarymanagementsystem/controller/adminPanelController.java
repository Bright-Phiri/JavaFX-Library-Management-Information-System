/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static librarymanagementsystem.controller.mainController.checkLateFee;
import librarymanagementsystem.model.LoadStage;
import librarymanagementsystem.model.MailServer;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class adminPanelController implements Initializable {

    @FXML
    private BorderPane borderpane;
    @FXML
    private VBox homeButtonsPanel;
    @FXML
    private JFXButton back;
    @FXML
    private JFXButton home;
    @FXML
    private JFXButton books;
    @FXML
    private JFXButton settings;
    @FXML
    private JFXButton close;
    double x = 0, y = 0;
    @FXML
    private JFXButton export;
    @FXML
    private JFXButton lateFee;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        back.setTooltip(new Tooltip("Logout"));
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(4000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        checkLateFee("Set late fee in settings panel");
                    }
                });
                Thread.sleep(2000);
                if (MailServer.getMailServerInformation() == null) {
                    Platform.runLater(() -> {
                        Notification notification = new Notification("Information", "Mail server not configured", 3);
                    });
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        try {
            BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/dashBoard.fxml"));
            borderpane.setCenter(borderPane);
        } catch (IOException ex) {
            Logger.getLogger(adminPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        LoadStage stage = new LoadStage("/librarymanagementsystem/view/login.fxml", home);
        librarianController.isEditableMode = false;
    }

    @FXML
    private void loadHomePanel(ActionEvent event) throws IOException {
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/dashBoard.fxml"));
        borderpane.setCenter(borderPane);
        librarianController.isEditableMode = false;
    }

    @FXML
    private void loadLibrariansPanel(ActionEvent event) throws IOException {
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/librarian.fxml"));
        borderpane.setCenter(borderPane);
        librarianController.isEditableMode = false;
    }

    @FXML
    private void loadSettingsPanel(ActionEvent event) throws IOException {
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/adminSettings.fxml"));
        borderpane.setCenter(borderPane);
        librarianController.isEditableMode = false;
    }

    @FXML
    private void closeApp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to close the application ?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    private void stageDragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    private void stagePressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    private void exportData(ActionEvent event) throws IOException {
        exportDataAdminController.type = null;
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/exportDataAdmin.fxml"));
        borderpane.setCenter(borderPane);
        librarianController.isEditableMode = false;
    }

    @FXML
    private void loadTrackLateFeePanel(ActionEvent event) throws IOException {
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/trackLateFee.fxml"));
        borderpane.setCenter(borderPane);
        librarianController.isEditableMode = false;
    }
}

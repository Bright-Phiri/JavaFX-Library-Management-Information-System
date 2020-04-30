/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXButton;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class adminSettingsController implements Initializable {

    @FXML
    private Label minimise;
    @FXML
    private Label fullscreen;
    @FXML
    private Label unfullscreen;
    @FXML
    private Label close;
    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXButton adminInfor;
    @FXML
    private JFXButton signInOptions;
    @FXML
    private JFXButton feeButton;
    @FXML
    private JFXButton feeButton1;
    @FXML
    private JFXButton feeButton11;
    @FXML
    private JFXButton feeButton2;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tooltip closeApp = new Tooltip("Close");
        closeApp.setStyle("-fx-font-size:11");
        closeApp.setMinSize(20, 20);
        close.setTooltip(closeApp);
        Tooltip minimizeApp = new Tooltip("Minimize");
        minimizeApp.setStyle("-fx-font-size:11");
        minimizeApp.setMinSize(20, 20);
        minimise.setTooltip(minimizeApp);
        Tooltip fullScreen = new Tooltip("fullscreen");
        fullScreen.setStyle("-fx-font-size:11");
        fullScreen.setMinSize(20, 20);
        fullscreen.setTooltip(fullScreen);
        Tooltip exitFullSceen = new Tooltip("Exit full screen");
        exitFullSceen.setStyle("-fx-font-size:11");
        exitFullSceen.setMinSize(20, 20);
        unfullscreen.setTooltip(exitFullSceen);
        Image closeImage = new Image("/librarymanagementsystem/images/close.png");
        close.setGraphic(new ImageView(closeImage));
        Image unfullscreenImage = new Image("/librarymanagementsystem/images/unfullscreen.png");
        unfullscreen.setGraphic(new ImageView(unfullscreenImage));
        Image fullscreenImage = new Image("/librarymanagementsystem/images/fullscreen.png");
        fullscreen.setGraphic(new ImageView(fullscreenImage));
        Image minimizeImage = new Image("/librarymanagementsystem/images/minimize.png");
        minimise.setGraphic(new ImageView(minimizeImage));
        try {
            BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/editAdminDetails.fxml"));
            borderPane.setCenter(pane);
        } catch (IOException ex) {
            Logger.getLogger(adminSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void fullscreen(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (!stage.isFullScreen()) {
            stage.setFullScreen(true);
        }
    }

    @FXML
    private void unfullscreen(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        }
    }

    @FXML
    private void close(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void loadAdmininfo(ActionEvent event) throws IOException {
        BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/editAdminDetails.fxml"));
        borderPane.setCenter(pane);
    }

    @FXML
    private void loadSignInOptions(ActionEvent event) throws IOException {
        BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/changeAdminPassword.fxml"));
        borderPane.setCenter(pane);
    }

    @FXML
    private void loadChangeFeePanel(ActionEvent event) throws IOException {
        BorderPane pane = FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/changeFeePerDay.fxml"));
        borderPane.setCenter(pane);
    }

    @FXML
    private void loadMailServerSettingsPanel(ActionEvent event) throws IOException {
        BorderPane pane = FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/configureEmailServer.fxml"));
        borderPane.setCenter(pane);
    }

    @FXML
    private void backupDatabase(ActionEvent event) throws IOException {
        Connection connection = null;
        Statement statement = null;
        DirectoryChooser chooser = new DirectoryChooser();
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        File file1 = chooser.showDialog(adminInfor.getScene().getWindow());
        File file;
        File databaseFile;
        if (file1 != null) {
            file = new File(file1.toString() + "\\Database_Backup_" + dateFormat.format(new Date()));
            if (file.exists()) {
                databaseFile = new File(file.getAbsolutePath() + "\\Library.db");
            } else {
                file.mkdir();
                databaseFile = new File(file.getAbsolutePath() + "\\Library.db");
            }
            try {
                connection = DatabaseConnection.Connect();
                statement = connection.createStatement();
                statement.executeUpdate("backup to " + databaseFile);
                Notification notification = new Notification("Information", "Database backup completed", 3);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(adminSettingsController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            desktop.open(file);
                        } catch (IOException ex) {
                            Logger.getLogger(adminSettingsController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();
            } catch (SQLException ex) {
                Logger.getLogger(adminSettingsController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(adminSettingsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            Notification notification = new Notification("Information", "Database backup canceled", 3);
        }
    }

    @FXML
    private void loadChangeHourFeePanel(ActionEvent event) throws IOException {
        BorderPane pane = FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/changeFeePerHour.fxml"));
        borderPane.setCenter(pane);
    }
}

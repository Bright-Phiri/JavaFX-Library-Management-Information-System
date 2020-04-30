/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class exportDataAdminController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Label minimise;
    @FXML
    private Label fullscreen;
    @FXML
    private Label unfullscreen;
    @FXML
    private Label close;
    @FXML
    private ComboBox<String> reportType;
    public static String type = null;

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
            BorderPane borderpane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/exportToPdfAdmin.fxml"));
            borderPane.setCenter(borderpane);
        } catch (IOException ex) {
            Logger.getLogger(exportDataAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
        reportType.setItems(FXCollections.<String>observableArrayList("Librarian"));
        reportType.setOnAction((e) -> {
            type = reportType.getValue();
        });
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
    private void loadPDFExportPanel(ActionEvent event) throws IOException {
        BorderPane borderpane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/exportToPdfAdmin.fxml"));
        borderPane.setCenter(borderpane);
    }

    @FXML
    private void loadPDFExceltPanel(ActionEvent event) throws IOException {
        BorderPane borderP = FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/exportToExcelAdmin.fxml"));
        borderPane.setCenter(borderP);
    }

    @FXML
    private void loadWordExportPanel(ActionEvent event) throws IOException {
        BorderPane borderP = FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/exportToWordAdmin.fxml"));
        borderPane.setCenter(borderP);
    }

    private boolean isTypeSelected() {
        if (reportType.getValue() == null) {
            return false;
        }
        type = reportType.getValue();
        return true;
    }
}

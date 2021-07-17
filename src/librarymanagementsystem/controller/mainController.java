package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.LoadStage;
import librarymanagementsystem.model.MailServer;
import librarymanagementsystem.model.Notification;

public class mainController implements Initializable {

    @FXML
    private BorderPane borderpane;
    @FXML
    private JFXButton back;
    @FXML
    private JFXButton settings;
    @FXML
    private JFXButton close;
    @FXML
    private JFXButton books;
    @FXML
    private JFXButton home;
    @FXML
    private VBox homeButtonsPanel;
    double x = 0, y = 0;
    @FXML
    private JFXButton students;
    @FXML
    private JFXButton issuedBooks;
    @FXML
    private JFXButton retunbooks;
    @FXML
    private JFXButton allIssuedBooks;
    @FXML
    private JFXButton export;
    @FXML
    private JFXButton announcement;
    public static BorderPane pane;
    @FXML
    private JFXButton clearance;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pane = borderpane;
        back.setTooltip(new Tooltip("Logout"));
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(4000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        checkLateFee(" Late fee not set");
                    }
                });
                return null;
            }

            @Override
            protected void succeeded() {
                if (MailServer.getMailServerInformation() == null) {
                    Platform.runLater(() -> {
                        Notification notification = new Notification("Information", "Mail server not configured", 3);
                    });
                }
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        try {
            BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/dashBoard.fxml"));
            borderpane.setCenter(borderPane);
        } catch (IOException ex) {
            Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        File reportsFolder = new File(System.getProperty("user.home") + "//Documents//Reports");
        if (!reportsFolder.exists()) {
            reportsFolder.mkdir();
        }
    }

    @FXML
    private void loadSettingsPanel(ActionEvent event) throws IOException {
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/settings.fxml"));
        borderpane.setCenter(borderPane);
        booktDataEntryController.isinEditMode = false;
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
    private void logout(ActionEvent event) throws IOException {
        booktDataEntryController.isinEditMode = false;
        LoadStage stage = new LoadStage("/librarymanagementsystem/view/login.fxml", home);
    }

    @FXML
    private void loadBooksPanel(ActionEvent event) throws IOException {
        booktDataEntryController.isinEditMode = false;
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/allBooks.fxml"));
        borderpane.setCenter(borderPane);
        allBooksController.box.setVisible(true);
    }

    @FXML
    private void loadStudentPanel(ActionEvent event) throws IOException {
        booktDataEntryController.isinEditMode = false;
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/students.fxml"));
        borderpane.setCenter(borderPane);
    }

    @FXML
    private void loadIssueBooksPanel(ActionEvent event) throws IOException {
        booktDataEntryController.isinEditMode = false;
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/issueBooks.fxml"));
        borderpane.setCenter(borderPane);
    }

    @FXML
    private void loadReturnBooksPanel(ActionEvent event) throws IOException {
        booktDataEntryController.isinEditMode = false;
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/returnBook.fxml"));
        borderpane.setCenter(borderPane);
    }

    @FXML
    private void viewAllIssuedBooks(ActionEvent event) throws IOException {
        booktDataEntryController.isinEditMode = false;
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/viewIssuedBooks.fxml"));
        borderpane.setCenter(borderPane);
    }

    @FXML
    private void loadHomePanel(ActionEvent event) throws IOException {
        booktDataEntryController.isinEditMode = false;
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/dashBoard.fxml"));
        borderpane.setCenter(borderPane);
    }

    public static void checkLateFee(String message) {
        String query = "SELECT * FROM Account";
        String insertQuery = "INSERT INTO Account (LateFeePerDay,LateFeePerHour) VALUES (0.00,0.00)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement1 = connection.prepareStatement(insertQuery);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double lateFeePerDay = resultSet.getDouble(2);
                double lateFeePerHour = resultSet.getDouble(3);
                if (lateFeePerDay == 0.0 || lateFeePerHour == 0.0) {
                    Notification notification = new Notification("Information", message, 5);
                }
            } else {
                preparedStatement1.executeUpdate();
                Notification notification = new Notification("Information", message, 5);

            }
        } catch (SQLException ex) {
            Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    private void loadExportDataPanel(ActionEvent event) throws IOException {
        booktDataEntryController.isinEditMode = false;
        exportDataController.type = null;
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/exportData.fxml"));
        borderpane.setCenter(borderPane);
    }

    @FXML
    private void loadSendAnnouncementsPanel(ActionEvent event) throws IOException {
        booktDataEntryController.isinEditMode = false;
        exportDataController.type = null;
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/announcements.fxml"));
        borderpane.setCenter(borderPane);
    }

    @FXML
    private void loadClearancePanel(ActionEvent event) throws IOException {
        booktDataEntryController.isinEditMode = false;
        exportDataController.type = null;
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/clearance.fxml"));
        borderpane.setCenter(borderPane);
    }
}

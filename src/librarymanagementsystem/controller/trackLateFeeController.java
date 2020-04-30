/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import librarymanagementsystem.model.Alert;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.IssueDetails;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class trackLateFeeController implements Initializable {

    @FXML
    private BorderPane boarderpane;
    @FXML
    private Label minimise;
    @FXML
    private Label fullscreen;
    @FXML
    private Label unfullscreen;
    @FXML
    private Label close;
    @FXML
    private Label word;
    @FXML
    private ImageView spinner;
    @FXML
    private ProgressBar prograssBar;
    @FXML
    private Text totalFee;
    @FXML
    private TableView<IssueDetails> tableView;
    @FXML
    private TableColumn<IssueDetails, String> bookId;
    @FXML
    private TableColumn<IssueDetails, String> bookName;
    @FXML
    private TableColumn<IssueDetails, String> studentID;
    @FXML
    private TableColumn<IssueDetails, String> studentName;
    @FXML
    private TableColumn<IssueDetails, String> issuedDate;
    @FXML
    private TableColumn<IssueDetails, String> returnDate;
    @FXML
    private TableColumn<IssueDetails, Double> fee;
    @FXML
    private TableColumn<IssueDetails, String> issuedBy;
    ObservableList<IssueDetails> details = FXCollections.observableArrayList();
    @FXML
    private DatePicker date1;
    @FXML
    private DatePicker date2;

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
        intializeColumns();
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                int max = 40;
                for (int i = 0; i <= max; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(i, max);
                    Thread.sleep(25);
                }
                return max;
            }
        };
        prograssBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                spinner.setVisible(false);
                loadData();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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
    private void allRecords(ActionEvent event) {
        loadData();
    }

    @FXML
    private void dailyReport(ActionEvent event) {
        details.clear();
        Connection connection = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;
        String query1 = "SELECT * FROM Fee where Date = ?";
        String query2 = "SELECT SUM(LateFee) FROM Fee where Date = ?";
        if (date1.getValue() == null) {
            Alert alert = new Alert(AlertType.INFORMATION, "Information", "Please select Date");
            return;
        }
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement1 = connection.prepareStatement(query1);
            preparedStatement1.setString(1, date1.getValue().toString());
            preparedStatement2 = connection.prepareStatement(query2);
            preparedStatement2.setString(1, date1.getValue().toString());
            resultSet1 = preparedStatement1.executeQuery();
            resultSet2 = preparedStatement2.executeQuery();
            int totalFees = resultSet2.getInt(1);
            while (resultSet1.next()) {
                details.add(new IssueDetails(resultSet1.getString(1), resultSet1.getString(2), resultSet1.getString(3), resultSet1.getString(4), resultSet1.getString(5), resultSet1.getString(6), resultSet1.getDouble(7), resultSet1.getString(8)));
            }
            tableView.setItems(details);
            totalFee.setText(String.valueOf(totalFees) + ".00");
        } catch (SQLException ex) {
            Logger.getLogger(trackLateFeeController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet1 != null) {
                    resultSet1.close();
                }
                if (resultSet2 != null) {
                    resultSet2.close();
                }
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(trackLateFeeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void monthlyReport(ActionEvent event) {
        details.clear();
        Connection connection = null;
        PreparedStatement preparedStatement1 = null;
        ResultSet resultSet1 = null;
        String query1 = "SELECT * FROM Fee";
        int totalMonthlyFee = 0;
        if (date2.getValue() == null) {
            Alert alert = new Alert(AlertType.INFORMATION, "Information", "Please select Date");
            return;
        }
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement1 = connection.prepareStatement(query1);
            resultSet1 = preparedStatement1.executeQuery();
            while (resultSet1.next()) {
                LocalDate transactionDate = LocalDate.parse(resultSet1.getString("Date"));
                if (transactionDate.getYear() == date2.getValue().getYear() && transactionDate.getMonth().equals(date2.getValue().getMonth())) {
                    details.add(new IssueDetails(resultSet1.getString(1), resultSet1.getString(2), resultSet1.getString(3), resultSet1.getString(4), resultSet1.getString(5), resultSet1.getString(6), resultSet1.getDouble(7), resultSet1.getString(8)));
                    totalMonthlyFee+= resultSet1.getDouble("LateFee");
                }
            }
            tableView.setItems(details);
            totalFee.setText(String.valueOf(totalMonthlyFee) + ".00");
        } catch (SQLException ex) {
            Logger.getLogger(trackLateFeeController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet1 != null) {
                    resultSet1.close();
                }
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(trackLateFeeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void intializeColumns() {
        bookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        bookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        studentID.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        issuedDate.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        returnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        fee.setCellValueFactory(new PropertyValueFactory<>("fee"));
        issuedBy.setCellValueFactory(new PropertyValueFactory<>("user"));
    }

    private void loadData() {
        details.clear();
        Connection connection = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;
        String query1 = "SELECT * FROM Fee";
        String query2 = "SELECT SUM(LateFee) FROM Fee";
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement1 = connection.prepareStatement(query1);
            preparedStatement2 = connection.prepareStatement(query2);
            resultSet1 = preparedStatement1.executeQuery();
            resultSet2 = preparedStatement2.executeQuery();
            int totalFees = resultSet2.getInt(1);
            while (resultSet1.next()) {
                details.add(new IssueDetails(resultSet1.getString(1), resultSet1.getString(2), resultSet1.getString(3), resultSet1.getString(4), resultSet1.getString(5), resultSet1.getString(6), resultSet1.getDouble(7), resultSet1.getString(8)));
            }
            tableView.setItems(details);
            totalFee.setText(String.valueOf(totalFees) + ".00");
        } catch (SQLException ex) {
            Logger.getLogger(trackLateFeeController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet1 != null) {
                    resultSet1.close();
                }
                if (resultSet2 != null) {
                    resultSet2.close();
                }
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(trackLateFeeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

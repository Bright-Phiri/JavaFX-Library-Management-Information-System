/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.IssuedBook;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class clearanceController implements Initializable {

    @FXML
    private Label minimise;
    @FXML
    private Label fullscreen;
    @FXML
    private Label unfullscreen;
    @FXML
    private Label close;
    @FXML
    private JFXTextField studentID;
    @FXML
    private TableView<IssuedBook> longTermBooksTable;
    @FXML
    private TableColumn<IssuedBook, String> lStudentName;
    @FXML
    private TableColumn<IssuedBook, String> iBookName;
    @FXML
    private TableColumn<IssuedBook, String> iBorrowedDate;
    @FXML
    private TableColumn<IssuedBook, String> iDateReturned;
    @FXML
    private TableView<IssuedBook> shortTermBooksTable;
    @FXML
    private TableColumn<IssuedBook, String> sStudentName;
    @FXML
    private TableColumn<IssuedBook, String> sBookName;
    @FXML
    private TableColumn<IssuedBook, String> sIssuedTime;
    @FXML
    private TableColumn<IssuedBook, String> sReturnTime;
    ObservableList<IssuedBook> list1 = FXCollections.observableArrayList();
    ObservableList<IssuedBook> list2 = FXCollections.observableArrayList();

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
        initializeColumns();
        getData("SELECT * FROM IssueBook", list1, "IssuedDate", "ReturnDate", longTermBooksTable);
        getData("SELECT * FROM ShortTermBook", list2, "IssuedTime", "ReturnTime", shortTermBooksTable);
    }

    private void initializeColumns() {
        lStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        iBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        iBorrowedDate.setCellValueFactory(new PropertyValueFactory<>("issuedTime"));
        iDateReturned.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        sStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        sBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        sIssuedTime.setCellValueFactory(new PropertyValueFactory<>("issuedTime"));
        sReturnTime.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
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
    private void loadIssuedBookDetails(KeyEvent event) {
        filterData(list1, longTermBooksTable);
        filterData(list2, shortTermBooksTable);
    }

    public void filterData(ObservableList<IssuedBook> books, TableView tableView) {
        FilteredList<IssuedBook> filteredList = new FilteredList<>(books, p -> true);
        studentID.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super IssuedBook>) book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String toLowerCase = newValue.toLowerCase();
                if (book.getStudentID().toLowerCase().contains(toLowerCase)) {
                    return true;
                }
                tableView.setPlaceholder(new Text("No record  match your search"));
                return false;
            });
            SortedList<IssuedBook> sortedBooksList = new SortedList<>(filteredList);
            sortedBooksList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.getItems().setAll(sortedBooksList);
        }));
    }

    private void getData(String query, ObservableList<IssuedBook> list, String field1, String field2, TableView tableView) {
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        list.clear();
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                list.add(new IssuedBook(rs.getString("StudentID"), rs.getString("StudentName"), rs.getString("BookName"), rs.getString(field1), rs.getString(field2)));
            }
            tableView.getItems().setAll(list);
        } catch (SQLException ex) {
            Logger.getLogger(clearanceController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (pre != null) {
                    pre.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
}

package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class returnBookController implements Initializable {

    @FXML
    private Label minimise;
    @FXML
    private Label fullscreen;
    @FXML
    private Label unfullscreen;
    @FXML
    private Label close;
    @FXML
    private JFXTextField issuedIDInput;
    @FXML
    private ListView<String> listView;
    ObservableList<String> data = FXCollections.observableArrayList();
    @FXML
    private JFXTextField lateFee;
    @FXML
    private JFXButton submit;
    @FXML
    private JFXComboBox<String> issueSelection;
    String selectedValue;
    int id;
    int issuedId;
    @FXML
    private JFXButton renew;

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
        issuedIDInput.requestFocus();
        issueSelection.getItems().addAll(FXCollections.observableArrayList("Short Term", "Long Term"));
        viewShortTermBooksController shortTermBooksController = new viewShortTermBooksController();
        shortTermBooksController.updateFee();
        viewIssuedBooksController issuedBooksController = new viewIssuedBooksController();
        issuedBooksController.updateFee();

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
        if (issueSelection.getValue() == null) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", "Please select issue type");
            return;
        }
        switch (issueSelection.getValue()) {
            case "Short Term": {
                PreparedStatement pre1 = null;
                PreparedStatement pre2 = null;
                PreparedStatement pre3 = null;
                Connection conn = null;
                ResultSet rs1 = null;
                ResultSet rs2 = null;
                ResultSet rs3 = null;
                String bName = "";
                String sName = "";
                String sEmail = "";
                String sPhone = "";
                String query1 = "SELECT * FROM ShortTermBook WHERE IssuedID = ? COLLATE NOCASE";
                String query2 = "SELECT * FROM Book WHERE BookID = ?";
                String query3 = "SELECT * FROM Student WHERE StudentID = ?";
                if (event.getCode() == KeyCode.ENTER) {
                    try {
                        conn = DatabaseConnection.Connect();
                        if (issuedIDInput.getText().isEmpty()) {
                            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Field Validation", "The field is empty");
                            data.clear();
                            listView.getItems().setAll(data);
                            lateFee.clear();
                        } else {
                            pre1 = conn.prepareStatement(query1);
                            pre2 = conn.prepareStatement(query2);
                            pre3 = conn.prepareStatement(query3);
                            pre1.setString(1, issuedIDInput.getText());
                            rs1 = pre1.executeQuery();
                            if (rs1.next()) {
                                String bookID = rs1.getString("BookID");
                                String studentid = rs1.getString("StudentID");
                                String dateAndTime = rs1.getString("IssuedTime");
                                pre2.setString(1, bookID);
                                pre3.setString(1, studentid);
                                rs2 = pre2.executeQuery();
                                rs3 = pre3.executeQuery();
                                if (rs1.getDouble("Fee") > 0) {
                                    lateFee.setEditable(true);
                                    renew.setDisable(Boolean.TRUE);
                                } else {
                                    lateFee.clear();
                                    lateFee.setEditable(false);
                                    renew.setDisable(Boolean.FALSE);
                                }
                                while (rs2.next()) {
                                    bName = rs2.getString("Name");
                                }
                                while (rs3.next()) {
                                    sName = rs3.getString("Name");
                                    sEmail = rs3.getString("Email");
                                    sPhone = rs3.getString("Phone");
                                }
                                issuedId = Integer.parseInt(issuedIDInput.getText());
                                data.add("Issued ID               : " + issuedIDInput.getText());
                                data.add("ISSUED BOOK INFORMATION");
                                data.add("Book ISBN             : " + bookID);
                                data.add("Book Title              : " + bName);
                                data.add("Issued Time           : " + dateAndTime);
                                data.add("\nSTUDENT INFORMATION");
                                data.add("Student Name       : " + sName);
                                data.add("Student Email        : " + sEmail);
                                data.add("Student Phone      : " + sPhone);
                                data.add("\nLate Fee                 : " + rs1.getDouble("Fee"));
                                listView.getItems().setAll(data);
                                data.clear();
                            } else {
                                Notification notification = new Notification("Message", "No book found", 3);
                                issuedIDInput.clear();
                                listView.getItems().clear();
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e);
                    } finally {
                        try {
                            if (pre1 != null) {
                                pre1.close();
                            }
                            if (pre2 != null) {
                                pre2.close();
                            }
                            if (pre3 != null) {
                                pre3.close();
                            }
                            if (rs1 != null) {
                                rs1.close();
                            }
                            if (rs2 != null) {
                                rs2.close();
                            }
                            if (rs3 != null) {
                                rs3.close();
                            }
                            if (conn != null) {
                                conn.close();
                            }
                        } catch (SQLException e1) {
                            System.err.println(e1);
                        }
                    }
                }
                break;
            }
            case "Long Term": {
                PreparedStatement pre1 = null;
                PreparedStatement pre2 = null;
                PreparedStatement pre3 = null;
                Connection conn = null;
                ResultSet rs1 = null;
                ResultSet rs2 = null;
                ResultSet rs3 = null;
                String bName = "";
                String sName = "";
                String sEmail = "";
                String sPhone = "";
                String query1 = "SELECT * FROM IssueBook WHERE IssuedID = ? COLLATE NOCASE";
                String query2 = "SELECT * FROM Book WHERE BookID = ?";
                String query3 = "SELECT * FROM Student WHERE StudentID = ?";
                if (event.getCode() == KeyCode.ENTER) {
                    try {
                        conn = DatabaseConnection.Connect();
                        if (issuedIDInput.getText().isEmpty()) {
                            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Field Validation", "The field is empty");
                            data.clear();
                            listView.getItems().setAll(data);
                            lateFee.clear();
                        } else {
                            pre1 = conn.prepareStatement(query1);
                            pre2 = conn.prepareStatement(query2);
                            pre3 = conn.prepareStatement(query3);
                            pre1.setString(1, issuedIDInput.getText());
                            rs1 = pre1.executeQuery();
                            if (rs1.next()) {
                                String bookID = rs1.getString("BookID");
                                String studentid = rs1.getString("StudentID");
                                String dateAndTime = rs1.getString("IssuedDate");
                                pre2.setString(1, bookID);
                                pre3.setString(1, studentid);
                                rs2 = pre2.executeQuery();
                                rs3 = pre3.executeQuery();
                                if (rs1.getDouble("Fee") > 0) {
                                    lateFee.setEditable(true);
                                    renew.setDisable(Boolean.TRUE);
                                } else {
                                    lateFee.clear();
                                    lateFee.setEditable(false);
                                    renew.setDisable(Boolean.FALSE);
                                }
                                while (rs2.next()) {
                                    bName = rs2.getString("Name");
                                }
                                while (rs3.next()) {
                                    sName = rs3.getString("Name");
                                    sEmail = rs3.getString("Email");
                                    sPhone = rs3.getString("Phone");
                                }
                                issuedId = Integer.parseInt(issuedIDInput.getText());
                                data.add("Issued ID               : " + issuedIDInput.getText());
                                data.add("ISSUED BOOK INFORMATION");
                                data.add("Book ISBN             : " + bookID);
                                data.add("Book Title              : " + bName);
                                data.add("Issued on " + dateAndTime);
                                data.add("\nSTUDENT INFORMATION");
                                data.add("Student Name       : " + sName);
                                data.add("Student Email        : " + sEmail);
                                data.add("Student Phone      : " + sPhone);
                                data.add("\nLate Fee                 : " + rs1.getDouble("Fee"));
                                listView.getItems().setAll(data);
                                data.clear();
                            } else {
                                Notification notification = new Notification("Message", "No book found", 3);
                                issuedIDInput.clear();
                                listView.getItems().clear();
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e);
                    } finally {
                        try {
                            if (pre1 != null) {
                                pre1.close();
                            }
                            if (pre2 != null) {
                                pre2.close();
                            }
                            if (pre3 != null) {
                                pre3.close();
                            }
                            if (rs1 != null) {
                                rs1.close();
                            }
                            if (rs2 != null) {
                                rs2.close();
                            }
                            if (rs3 != null) {
                                rs3.close();
                            }
                            if (conn != null) {
                                conn.close();
                            }
                        } catch (SQLException e1) {
                            System.err.println(e1);
                        }
                    }
                }
                break;
            }
        }
    }

    @FXML
    private void submitBook(ActionEvent event) {
        if (issueSelection.getValue() == null) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", "Please select issue type");
            return;
        }
        switch (selectedValue) {
            case "Short Term": {
                int count = 0;
                int totalNumberOfBooks;
                PreparedStatement pre1 = null;
                PreparedStatement pre2 = null;
                PreparedStatement pre3 = null;
                PreparedStatement pre4 = null;
                PreparedStatement pre5 = null;
                PreparedStatement pre6 = null;
                PreparedStatement pre7 = null;
                Connection conn = null;
                ResultSet rs = null;
                String query1 = "SELECT * FROM ShortTermBook WHERE IssuedID = ?";
                String query2 = "UPDATE Book SET Availability = 'Available' WHERE BookID = ?";
                String query3 = "SELECT RemainingBooks FROM Book WHERE BookID = ?";
                String query4 = "DELETE FROM ShortTermBook WHERE IssuedID = ?";
                String query5 = "SELECT Quantity FROM Book WHERE BookID = ?";
                String query6 = "UPDATE Book SET RemainingBooks = ? WHERE BookID = ?";
                String query7 = "INSERT INTO Fee VALUES (?,?,?,?,?,?,?,?,?)";
                if (validateField() && validateLateFee()) {
                    try {
                        conn = DatabaseConnection.Connect();
                        if (issuedIDInput.getText().isEmpty()) {
                            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Field Validation", "Issued ID field is empty");
                        } else {
                            pre1 = conn.prepareStatement(query1);
                            pre2 = conn.prepareStatement(query2);
                            pre3 = conn.prepareStatement(query3);
                            pre4 = conn.prepareStatement(query4);
                            pre5 = conn.prepareStatement(query5);
                            pre6 = conn.prepareStatement(query6);
                            pre1.setString(1, issuedIDInput.getText().trim());
                            rs = pre1.executeQuery();
                            if (!lateFee.isEditable()) {
                                if (rs.next()) {
                                    String bookId = rs.getString("BookID");
                                    pre3.setString(1, bookId);
                                    pre5.setString(1, bookId);
                                    ResultSet rs2 = pre3.executeQuery();
                                    ResultSet rs3 = pre5.executeQuery();
                                    count = rs2.getInt(1);
                                    totalNumberOfBooks = rs3.getInt("Quantity");
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation");
                                    alert.setContentText("Are you sure you want to return this book ?");
                                    alert.setHeaderText(null);
                                    Optional<ButtonType> option = alert.showAndWait();
                                    if (option.get() == ButtonType.OK) {
                                        pre4.setString(1, issuedIDInput.getText());
                                        pre4.executeUpdate();
                                        if (count < totalNumberOfBooks || count == 0) {
                                            count++;
                                            pre2.setString(1, bookId);
                                            pre2.executeUpdate();
                                            pre6.setInt(1, count);
                                            pre6.setString(2, bookId);
                                            pre6.executeUpdate();
                                        }
                                        if (count == totalNumberOfBooks) {
                                        }
                                        pre7 = conn.prepareStatement(query7);
                                        pre7.setString(1, rs.getString("BookID"));
                                        pre7.setString(2, rs.getString("BookName"));
                                        pre7.setString(3, rs.getString("StudentID"));
                                        pre7.setString(4, rs.getString("StudentName"));
                                        pre7.setString(5, rs.getString("IssuedTime"));
                                        pre7.setString(6, rs.getString("ReturnTime"));
                                        pre7.setDouble(7, rs.getDouble("Fee"));
                                        pre7.setString(8, loginController.userName);
                                        pre7.setString(9, LocalDate.now().toString());
                                        pre7.executeUpdate();
                                        Notification notification = new Notification("Message", "Book successfully submitted", 3);
                                        issuedIDInput.clear();
                                        lateFee.clear();
                                        listView.getItems().setAll(data);
                                        issuedIDInput.requestFocus();
                                    }
                                    rs.close();
                                } else {
                                    Notification notification = new Notification("Message", "No book found", 3);
                                    issuedIDInput.clear();
                                }
                                return;
                            }
                            if (Double.valueOf(lateFee.getText()) == rs.getDouble("Fee")) {
                                if (rs.next()) {
                                    String bookId = rs.getString("BookID");
                                    pre3.setString(1, bookId);
                                    pre5.setString(1, bookId);
                                    ResultSet rs2 = pre3.executeQuery();
                                    ResultSet rs3 = pre5.executeQuery();
                                    count = rs2.getInt(1);
                                    totalNumberOfBooks = rs3.getInt("Quantity");
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation");
                                    alert.setContentText("Are you sure you want to return this book ?");
                                    alert.setHeaderText(null);
                                    Optional<ButtonType> option = alert.showAndWait();
                                    if (option.get() == ButtonType.OK) {
                                        pre4.setString(1, issuedIDInput.getText());
                                        pre4.executeUpdate();
                                        if (count < totalNumberOfBooks || count == 0) {
                                            count++;
                                            pre2.setString(1, bookId);
                                            pre2.executeUpdate();
                                            pre6.setInt(1, count);
                                            pre6.setString(2, bookId);
                                            pre6.executeUpdate();
                                        }
                                        if (count == totalNumberOfBooks) {
                                        }
                                        pre7 = conn.prepareStatement(query7);
                                        pre7.setString(1, rs.getString("BookID"));
                                        pre7.setString(2, rs.getString("BookName"));
                                        pre7.setString(3, rs.getString("StudentID"));
                                        pre7.setString(4, rs.getString("StudentName"));
                                        pre7.setString(5, rs.getString("IssuedTime"));
                                        pre7.setString(6, rs.getString("ReturnTime"));
                                        pre7.setDouble(7, rs.getDouble("Fee"));
                                        pre7.setString(8, loginController.userName);
                                        pre7.setString(9, LocalDate.now().toString());
                                        pre7.executeUpdate();
                                        Notification notification = new Notification("Message", "Book successfully submitted", 3);
                                        issuedIDInput.clear();
                                        lateFee.clear();
                                        listView.getItems().setAll(data);
                                        issuedIDInput.requestFocus();
                                    }
                                    rs.close();
                                } else {
                                    Notification notification = new Notification("Message", "No book found", 3);
                                    issuedIDInput.clear();
                                }
                            } else if (Double.valueOf(lateFee.getText()) < rs.getDouble("Fee")) {
                                Notification notification = new Notification("Message", "Clear the late fee amount to return a book", 3);
                            } else if (Double.valueOf(lateFee.getText()) > rs.getDouble("Fee")) {
                                Notification notification = new Notification("Message", "Enter the exact late fee to return a book", 3);
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e);
                    } finally {
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                            if (pre1 != null) {
                                pre1.close();
                            }
                            if (pre2 != null) {
                                pre2.close();
                            }
                            if (pre3 != null) {
                                pre3.close();
                            }
                            if (pre4 != null) {
                                pre4.close();
                            }
                            if (pre5 != null) {
                                pre5.close();
                            }
                            if (pre6 != null) {
                                pre6.close();
                            }
                            if (pre7 != null) {
                                pre7.close();
                            }
                            if (conn != null) {
                                conn.close();
                            }
                        } catch (SQLException e1) {
                            System.err.println(e1);
                        }
                    }
                }
                break;
            }
            case "Long Term": {
                int count = 0;
                int totalNumberOfBooks;
                PreparedStatement pre1 = null;
                PreparedStatement pre2 = null;
                PreparedStatement pre3 = null;
                PreparedStatement pre4 = null;
                PreparedStatement pre5 = null;
                PreparedStatement pre6 = null;
                PreparedStatement pre7 = null;
                Connection conn = null;
                ResultSet rs = null;
                String query1 = "SELECT * FROM IssueBook WHERE IssuedID = ?";
                String query2 = "UPDATE Book SET Availability = 'Available' WHERE BookID = ?";
                String query3 = "SELECT RemainingBooks FROM Book WHERE BookID = ?";
                String query4 = "DELETE FROM IssueBook WHERE IssuedID = ?";
                String query5 = "SELECT Quantity FROM Book WHERE BookID = ?";
                String query6 = "UPDATE Book SET RemainingBooks = ? WHERE BookID = ?";
                String query7 = "INSERT INTO Fee VALUES (?,?,?,?,?,?,?,?,?)";
                if (validateField() && validateLateFee()) {
                    try {
                        conn = DatabaseConnection.Connect();
                        if (issuedIDInput.getText().isEmpty()) {
                            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Field Validation", "Issued ID field is empty");
                        } else {
                            pre1 = conn.prepareStatement(query1);
                            pre2 = conn.prepareStatement(query2);
                            pre3 = conn.prepareStatement(query3);
                            pre4 = conn.prepareStatement(query4);
                            pre5 = conn.prepareStatement(query5);
                            pre6 = conn.prepareStatement(query6);
                            pre7 = conn.prepareStatement(query7);
                            pre7.setString(9, LocalDate.now().toString());
                            pre1.setString(1, issuedIDInput.getText().trim());
                            rs = pre1.executeQuery();
                            if (!lateFee.isEditable()) {
                                if (rs.next()) {
                                    String bookId = rs.getString("BookID");
                                    pre3.setString(1, bookId);
                                    pre5.setString(1, bookId);
                                    ResultSet rs2 = pre3.executeQuery();
                                    ResultSet rs3 = pre5.executeQuery();
                                    count = rs2.getInt(1);
                                    totalNumberOfBooks = rs3.getInt("Quantity");
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation");
                                    alert.setContentText("Are you sure you want to return this book ?");
                                    alert.setHeaderText(null);
                                    Optional<ButtonType> option = alert.showAndWait();
                                    if (option.get() == ButtonType.OK) {
                                        pre4.setString(1, issuedIDInput.getText());
                                        pre4.executeUpdate();
                                        if (count < totalNumberOfBooks || count == 0) {
                                            count++;
                                            pre2.setString(1, bookId);
                                            pre2.executeUpdate();
                                            pre6.setInt(1, count);
                                            pre6.setString(2, bookId);
                                            pre6.executeUpdate();
                                        }
                                        if (count == totalNumberOfBooks) {
                                        }
                                        pre7.setString(1, rs.getString("BookID"));
                                        pre7.setString(2, rs.getString("BookName"));
                                        pre7.setString(3, rs.getString("StudentID"));
                                        pre7.setString(4, rs.getString("StudentName"));
                                        pre7.setString(5, rs.getString("IssuedDate"));
                                        pre7.setString(6, rs.getString("ReturnDate"));
                                        pre7.setDouble(7, rs.getDouble("Fee"));
                                        pre7.setString(8, loginController.userName);
                                        pre7.setString(9, LocalDate.now().toString());
                                        pre7.executeUpdate();
                                        Notification notification = new Notification("Message", "Book successfully submitted", 3);
                                        issuedIDInput.clear();
                                        lateFee.clear();
                                        listView.getItems().setAll(data);
                                        issuedIDInput.requestFocus();
                                    }
                                    rs.close();
                                } else {
                                    Notification notification = new Notification("Message", "No book found", 3);
                                    issuedIDInput.clear();
                                }
                                return;
                            }
                            if (Double.valueOf(lateFee.getText()) == rs.getDouble("Fee")) {
                                if (rs.next()) {
                                    String bookId = rs.getString("BookID");
                                    pre3.setString(1, bookId);
                                    pre5.setString(1, bookId);
                                    ResultSet rs2 = pre3.executeQuery();
                                    ResultSet rs3 = pre5.executeQuery();
                                    count = rs2.getInt(1);
                                    totalNumberOfBooks = rs3.getInt("Quantity");
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation");
                                    alert.setContentText("Are you sure you want to return this book ?");
                                    alert.setHeaderText(null);
                                    Optional<ButtonType> option = alert.showAndWait();
                                    if (option.get() == ButtonType.OK) {
                                        pre4.setString(1, issuedIDInput.getText());
                                        pre4.executeUpdate();
                                        if (count < totalNumberOfBooks || count == 0) {
                                            count++;
                                            pre2.setString(1, bookId);
                                            pre2.executeUpdate();
                                            pre6.setInt(1, count);
                                            pre6.setString(2, bookId);
                                            pre6.executeUpdate();
                                        }
                                        if (count == totalNumberOfBooks) {
                                        }
                                        Notification notification = new Notification("Message", "Book successfully submitted", 3);
                                        issuedIDInput.clear();
                                        lateFee.clear();
                                        listView.getItems().setAll(data);
                                        issuedIDInput.requestFocus();
                                        pre7.setString(1, rs.getString("BookID"));
                                        pre7.setString(2, rs.getString("BookName"));
                                        pre7.setString(3, rs.getString("StudentID"));
                                        pre7.setString(4, rs.getString("StudentName"));
                                        pre7.setString(5, rs.getString("IssuedDate"));
                                        pre7.setString(6, rs.getString("ReturnDate"));
                                        pre7.setDouble(7, rs.getDouble("Fee"));
                                        pre7.setString(8, loginController.userName);
                                        pre7.setString(9, LocalDate.now().toString());
                                        pre7.executeUpdate();
                                    }
                                    rs.close();
                                } else {
                                    Notification notification = new Notification("Message", "No book found", 3);
                                    issuedIDInput.clear();
                                }
                            } else if (Double.valueOf(lateFee.getText()) < rs.getDouble("Fee")) {
                                Notification notification = new Notification("Message", "Clear the late fee amount to return a book", 3);
                            } else if (Double.valueOf(lateFee.getText()) > rs.getDouble("Fee")) {
                                Notification notification = new Notification("Message", "Enter the exact late fee to return a book", 3);
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e);
                    } finally {
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                            if (pre1 != null) {
                                pre1.close();
                            }
                            if (pre2 != null) {
                                pre2.close();
                            }
                            if (pre3 != null) {
                                pre3.close();
                            }
                            if (pre4 != null) {
                                pre4.close();
                            }
                            if (pre5 != null) {
                                pre5.close();
                            }
                            if (pre6 != null) {
                                pre6.close();
                            }
                            if (pre7 != null) {
                                pre7.close();
                            }
                            if (conn != null) {
                                conn.close();
                            }
                        } catch (SQLException e1) {
                            System.err.println(e1);
                        }
                    }
                }
                break;
            }
        }
    }

    private boolean validateLateFee() {
        if (!lateFee.isEditable()) {
            return true;
        }
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(lateFee.getText());
        if (matcher.find() && matcher.group().equals(lateFee.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "Error", "Late fee is invalid");
            lateFee.clear();
            return false;
        }
    }

    private boolean validateField() {
        if (!lateFee.isEditable()) {
            return true;
        }
        if (lateFee.getText().isEmpty()) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", "Pleae enter in all fields");
            return false;
        }
        return true;
    }

    @FXML
    private void selectIssueType(ActionEvent event) {
        if (issueSelection.getValue().equals("Short Term")) {
            selectedValue = "Short Term";
        }
        if (issueSelection.getValue().equals("Long Term")) {
            selectedValue = "Long Term";
        }
    }

    @FXML
    private void renewBook(ActionEvent event) {
        String query = "UPDATE IssueBook SET IssuedDate = ?,ReturnDate = ? WHERE IssuedID = ?";
        String query1 = "UPDATE ShortTermBook SET IssuedTime = ?,ReturnTime = ? WHERE IssuedID = ?";
        PreparedStatement pre1 = null;
        Connection connection = null;
        LocalDate issuedDate = LocalDate.now();
        LocalDate afterFourteenDays = LocalDate.now().plusDays(14);
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);
        LocalDateTime issuedTime = LocalDateTime.now();
        LocalDateTime after2Hours = LocalDateTime.now().plusHours(2);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Renew Book");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to renew this book?");
        Optional<ButtonType> opt = alert.showAndWait();
        if (opt.get() == ButtonType.OK) {
            switch (issueSelection.getValue()) {
                case "Long Term": {
                    try {
                        connection = DatabaseConnection.Connect();
                        pre1 = connection.prepareStatement(query);
                        pre1.setString(1, issuedDate.toString());
                        pre1.setString(2, afterFourteenDays.toString());
                        pre1.setInt(3, issuedId);
                        pre1.executeUpdate();
                        Notification notification = new Notification("Message", "Books suceessfully renewed", 3);
                        issuedIDInput.clear();
                        lateFee.clear();
                        listView.getItems().setAll(data);
                        issuedIDInput.requestFocus();
                    } catch (SQLException ex) {
                        Logger.getLogger(returnBookController.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            if (pre1 != null) {
                                pre1.close();
                            }

                            if (connection != null) {
                                connection.close();
                            }
                        } catch (SQLException e1) {
                            System.err.println(e1);
                        }
                    }
                    break;
                }
                case "Short Term": {
                    try {
                        connection = DatabaseConnection.Connect();
                        pre1 = connection.prepareStatement(query1);
                        pre1.setString(1, formatter.format(issuedTime));
                        pre1.setString(2, formatter.format(after2Hours));
                        pre1.setInt(3, issuedId);
                        pre1.executeUpdate();
                        Notification notification = new Notification("Message", "Books suceessfully renewed", 3);
                        issuedIDInput.clear();
                        lateFee.clear();
                        listView.getItems().setAll(data);
                        issuedIDInput.requestFocus();
                    } catch (SQLException ex) {
                        Logger.getLogger(returnBookController.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            if (pre1 != null) {
                                pre1.close();
                            }

                            if (connection != null) {
                                connection.close();
                            }
                        } catch (SQLException e1) {
                            System.err.println(e1);
                        }
                    }
                    break;
                }
            }
        }
    }
}

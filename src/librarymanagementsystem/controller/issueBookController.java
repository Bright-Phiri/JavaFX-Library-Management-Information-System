package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXButton;
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
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class issueBookController implements Initializable {

    @FXML
    private Label minimise;
    @FXML
    private Label fullscreen;
    @FXML
    private Label unfullscreen;
    @FXML
    private Label close;
    @FXML
    private JFXTextField bookSearchField;
    @FXML
    private Text bookName;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookPublisher;
    @FXML
    private Text availability;
    @FXML
    private JFXTextField studentSearchTextField;
    @FXML
    private Text studentName;
    @FXML
    private Text studentEmail;
    @FXML
    private Text contact;
    String boName = "";
    String stuName = "";
    @FXML
    private JFXButton issueBook;

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
    }

    private void clearFieldsAndLabels() {
        bookSearchField.clear();
        studentSearchTextField.clear();
        bookName.setText("Book Title");
        bookAuthor.setText("Book Author");
        bookPublisher.setText("Book Publisher");
        availability.setText("Availability");
        studentName.setText("Student Name");
        studentEmail.setText("Email Address");
        contact.setText("Contact");
        boName = "";
        stuName = "";
    }

    private boolean checkIfStudentOwnsBook(String query) {
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            pre.setString(1, bookSearchField.getText());
            pre.setString(2, studentSearchTextField.getText());
            rs = pre.executeQuery();
            if (rs.next()) {
                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", studentName.getText() + " is already hoding this book");
                clearFieldsAndLabels();
                bookSearchField.requestFocus();
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e);
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
            } catch (SQLException e1) {
                System.err.println(e1);
            }
        }
        return true;
    }

    @FXML
    private void searchBook(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            boName = "";
            PreparedStatement pre = null;
            Connection conn = null;
            ResultSet rs = null;
            String query = "SELECT * FROM Book WHERE BookID = ? ";
            try {
                conn = DatabaseConnection.Connect();
                if (bookSearchField.getText().isEmpty()) {
                    librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Field validation", "The field is empty");
                } else {
                    pre = conn.prepareStatement(query);
                    pre.setString(1, bookSearchField.getText().trim());
                    rs = pre.executeQuery();
                    if (rs.next()) {
                        boName += rs.getString(2);
                        Notification notification = new Notification("Message", "Book found", 3);
                        String bName = rs.getString("Name");
                        String bAuthor = rs.getString("Author");
                        String bPublisher = rs.getString("Publisher");
                        String bAvailability = rs.getString("Availability");
                        if (bAvailability.equals("Not Available")) {
                            bookSearchField.clear();
                            bookName.setText(bName);
                            bookAuthor.setText(bAuthor);
                            bookPublisher.setText(bPublisher);
                            availability.setText(bAvailability);
                        } else {
                            bookName.setText(bName);
                            bookAuthor.setText(bAuthor);
                            bookPublisher.setText(bPublisher);
                            availability.setText(bAvailability);
                            studentSearchTextField.requestFocus();
                        }
                    } else {
                        bookSearchField.clear();
                        bookName.setText("Book Title");
                        bookAuthor.setText("Book Author");
                        bookPublisher.setText("Book Publisher");
                        availability.setText("Availability");
                        Notification notification = new Notification("Message", "No such book in the system", 3);
                    }
                }
            } catch (SQLException e) {
                System.err.println(e);
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
                } catch (SQLException e1) {
                    System.err.println(e1);
                }
            }
        }
    }

    @FXML
    private void searchStudent(KeyEvent event) {
        stuName = "";
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Student WHERE StudentID = ? COLLATE NOCASE";
        if (event.getCode() == KeyCode.ENTER) {
            try {
                conn = DatabaseConnection.Connect();
                if (studentSearchTextField.getText().isEmpty()) {
                    librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Field validation", "The field is empty");
                } else {
                    pre = conn.prepareStatement(query);
                    pre.setString(1, studentSearchTextField.getText().trim());
                    rs = pre.executeQuery();
                    if (rs.next()) {
                        stuName += rs.getString(2);
                        Notification notification = new Notification("Message", "Student found", 3);
                        String sName = rs.getString("Name");
                        String sEmail = rs.getString("Email");
                        String sPhone = rs.getString("Phone");
                        studentName.setText(sName);
                        studentEmail.setText(sEmail);
                        contact.setText(sPhone);
                    } else {
                        studentSearchTextField.clear();
                        studentName.setText("Student Name");
                        studentEmail.setText("Email Address");
                        contact.setText("Contact");
                        Notification notification = new Notification("Message", "Student not found", 3);
                    }
                }
            } catch (SQLException e) {
                System.err.println(e);
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
                } catch (SQLException e1) {
                    System.err.println(e1);
                }

            }
        }
    }

    @FXML
    private void issueBook(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSet = null;
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;
        PreparedStatement pre1 = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre3 = null;
        PreparedStatement pre4 = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Book WHERE BookID = ?";
        String selectLongTermBorrowedTime = "SELECT COUNT(StudentID) FROM IssueBook WHERE StudentID = ?";
        String selectShortTermBorrowedTime = "SELECT COUNT(StudentID) FROM ShortTermBook WHERE StudentID = ?";
        if (bookSearchField.getText().isEmpty() || studentSearchTextField.getText().isEmpty() || studentName.getText().equals("Student Name") || bookName.getText().equals("Book Name")) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", "Please enter in all fields");
        } else {
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement1 = connection.prepareStatement(selectLongTermBorrowedTime);
                preparedStatement2 = connection.prepareStatement(selectShortTermBorrowedTime);
                preparedStatement.setString(1, bookSearchField.getText());
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    switch (resultSet.getString("Section")) {
                        case "Long Loan Book": {
                            preparedStatement1.setString(1, studentSearchTextField.getText());
                            resultSet1 = preparedStatement1.executeQuery();
                            int borrowedTimes = resultSet1.getInt(1);
                            if (borrowedTimes == 0 || borrowedTimes <= 2) {
                                LocalDate issuedDate = LocalDate.now();
                                int count;
                                String query1 = "INSERT INTO IssueBook (BookID,BookName,StudentID,StudentName,IssuedDate,ReturnDate,Days) VALUES (?,?,?,?,?,?,?)";
                                String query2 = "UPDATE Book SET Availability = 'Not Available' WHERE BookID = ? COLLATE NOCASE";
                                String query3 = "SELECT RemainingBooks FROM Book WHERE BookID = ? COLLATE NOCASE";
                                String query4 = "UPDATE Book SET RemainingBooks = ? WHERE BookID = ? COLLATE NOCASE";
                                try {
                                    if (checkLateFee()) {
                                        LocalDate afterFourteenDays = LocalDate.now().plusDays(14);
                                        pre1 = connection.prepareStatement(query1);
                                        pre2 = connection.prepareStatement(query2);
                                        pre3 = connection.prepareStatement(query3);
                                        pre1.setString(1, bookSearchField.getText().trim());
                                        pre1.setString(2, boName);
                                        pre1.setString(3, studentSearchTextField.getText().trim());
                                        pre1.setString(4, stuName);
                                        pre1.setString(5, issuedDate.toString());
                                        pre1.setString(6, afterFourteenDays.toString());
                                        pre1.setInt(7, 14);
                                        pre2.setString(1, bookSearchField.getText().trim());
                                        pre3.setString(1, bookSearchField.getText().trim());
                                        rs = pre3.executeQuery();
                                        count = rs.getInt("RemainingBooks");
                                        if (checkIfStudentOwnsBook("SELECT * FROM IssueBook WHERE BookID = ? AND StudentID = ? COLLATE NOCASE") && checkIfStudentOwnsBook("SELECT * FROM ShortTermBook WHERE BookID = ? AND StudentID = ? COLLATE NOCASE")) {
                                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                            alert.setTitle("Confirmation");
                                            alert.setContentText("Are you sure you want to issue " + bookName.getText() + " to " + studentName.getText() + " ?");
                                            alert.setHeaderText(null);
                                            Optional<ButtonType> option = alert.showAndWait();
                                            if (option.get() == ButtonType.OK) {
                                                count--;
                                                pre1.executeUpdate();
                                                pre4 = connection.prepareStatement(query4);
                                                pre4.setInt(1, count);
                                                pre4.setString(2, bookSearchField.getText());
                                                pre4.executeUpdate();
                                                if (count == 0) {
                                                    pre2.executeUpdate();
                                                }
                                                Notification notification = new Notification("Message", "Book issue successfully completed", 3);
                                                bookSearchField.clear();
                                                studentSearchTextField.clear();
                                                bookName.setText("Book Title");
                                                bookAuthor.setText("Book Author");
                                                bookPublisher.setText("Book Publisher");
                                                availability.setText("Availability");
                                                studentName.setText("Student Name");
                                                studentEmail.setText("Email Address");
                                                contact.setText("Contact");
                                                stuName = "";
                                                boName = "";
                                                bookSearchField.requestFocus();
                                            }
                                        }
                                    }
                                } catch (SQLException exception) {
                                    System.err.println(exception);
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
                                        if (pre4 != null) {
                                            pre4.close();
                                        }
                                        if (rs != null) {
                                            rs.close();
                                        }
                                    } catch (SQLException e1) {
                                        System.err.println(e1);
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
                                            if (pre4 != null) {
                                                pre4.close();
                                            }
                                            if (rs != null) {
                                                rs.close();
                                            }
                                        } catch (SQLException e1) {
                                            System.err.println(e1);
                                        }
                                    }
                                }
                            } else {
                                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", "Maximum number of borrowed books reached");
                                clearFieldsAndLabels();
                            }
                            break;
                        }
                        case "Short Loan Book": {
                            preparedStatement2.setString(1, studentSearchTextField.getText());
                            resultSet2 = preparedStatement2.executeQuery();
                            int borrowedTimes = resultSet2.getInt(1);
                            if (borrowedTimes == 0 || borrowedTimes <= 1) {
                                int count;
                                String query1 = "INSERT INTO ShortTermBook (BookID,BookName,StudentID,StudentName,IssuedTime,ReturnTime,Hours) VALUES (?,?,?,?,?,?,?)";
                                String query2 = "UPDATE Book SET Availability = 'Not Available' WHERE BookID = ? COLLATE NOCASE";
                                String query3 = "SELECT RemainingBooks FROM Book WHERE BookID = ? COLLATE NOCASE";
                                String query4 = "UPDATE Book SET RemainingBooks = ? WHERE BookID = ? COLLATE NOCASE";
                                try {
                                    if (checkLateFee()) {
                                        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,FormatStyle.SHORT);
                                        LocalDateTime issuedTime = LocalDateTime.now();
                                        LocalDateTime after2Hours = LocalDateTime.now().plusHours(2);
                                        pre1 = connection.prepareStatement(query1);
                                        pre2 = connection.prepareStatement(query2);
                                        pre3 = connection.prepareStatement(query3);
                                        pre1.setString(1, bookSearchField.getText().trim());
                                        pre1.setString(2, boName);
                                        pre1.setString(3, studentSearchTextField.getText().trim());
                                        pre1.setString(4, stuName);
                                        pre1.setString(5, formatter.format(issuedTime));
                                        pre1.setString(6, formatter.format(after2Hours));
                                        pre1.setInt(7, 2);
                                        pre2.setString(1, bookSearchField.getText().trim());
                                        pre3.setString(1, bookSearchField.getText().trim());
                                        rs = pre3.executeQuery();
                                        count = rs.getInt("RemainingBooks");
                                        if (checkIfStudentOwnsBook("SELECT * FROM ShortTermBook WHERE BookID = ? AND StudentID = ? COLLATE NOCASE") && checkIfStudentOwnsBook("SELECT * FROM IssueBook WHERE BookID = ? AND StudentID = ? COLLATE NOCASE")) {
                                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                            alert.setTitle("Confirmation");
                                            alert.setContentText("Are you sure you want to issue " + bookName.getText() + " to " + studentName.getText() + " ?");
                                            alert.setHeaderText(null);
                                            Optional<ButtonType> option = alert.showAndWait();
                                            if (option.get() == ButtonType.OK) {
                                                count--;
                                                pre4 = connection.prepareStatement(query4);
                                                pre4.setInt(1, count);
                                                pre4.setString(2, bookSearchField.getText());
                                                pre4.executeUpdate();
                                                pre1.executeUpdate();
                                                if (count == 0) {
                                                    pre2.executeUpdate();
                                                }
                                                Notification notification = new Notification("Message", "Book issue successfully completed", 3);
                                                bookSearchField.clear();
                                                studentSearchTextField.clear();
                                                bookName.setText("Book Title");
                                                bookAuthor.setText("Book Author");
                                                bookPublisher.setText("Book Publisher");
                                                availability.setText("Availability");
                                                studentName.setText("Student Name");
                                                studentEmail.setText("Email Address");
                                                contact.setText("Contact");
                                                stuName = "";
                                                boName = "";
                                                bookSearchField.requestFocus();
                                            }
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
                                        if (pre4 != null) {
                                            pre4.close();
                                        }
                                        if (rs != null) {
                                            rs.close();
                                        }
                                    } catch (SQLException e1) {
                                        System.err.println(e1);
                                    }
                                }
                            } else {
                                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", "Maximum number of borrowed books reached");
                                clearFieldsAndLabels();
                            }
                            break;
                        }
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(issueBookController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (resultSet1 != null) {
                        resultSet1.close();
                    }
                    if (resultSet2 != null) {
                        resultSet2.close();
                    }
                    if (preparedStatement != null){
                        preparedStatement.close();
                    }
                    if (preparedStatement1 != null){
                        preparedStatement1.close();
                    }
                    if (preparedStatement2 != null){
                        preparedStatement2.close();
                    }
                    if (connection != null){
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(issueBookController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        clearFieldsAndLabels();
    }

    private boolean checkLateFee() {
        String query = "SELECT * FROM Account";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double lateFeePerDay = resultSet.getDouble(2);
                if (lateFeePerDay == 0.0) {
                    librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", "Late fee not set");
                    return false;

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(issueBookController.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();

                }
            } catch (SQLException ex) {
                Logger.getLogger(issueBookController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}

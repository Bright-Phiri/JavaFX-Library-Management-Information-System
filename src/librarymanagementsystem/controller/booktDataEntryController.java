package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import librarymanagementsystem.model.Book;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class booktDataEntryController implements Initializable {

    @FXML
    private TextField bookId;
    @FXML
    private TextField bookname;
    @FXML
    private TextField bookAuthor;
    @FXML
    private TextField bookPublusher;
    @FXML
    private TextField edition;
    @FXML
    private TextField quantity;
    @FXML
    private JFXButton save;
    @FXML
    private BorderPane pane1;
    @FXML
    private JFXButton cancel;
    @FXML
    private ComboBox<String> bookSection;
    
    private final ObservableList<String> bookIDS = FXCollections.observableArrayList();
    private final ObservableList<String> sections = FXCollections.observableArrayList("Short Loan Book","Long Loan Book");

    public static boolean isinEditMode = false;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bookId.setUserData("id");
        bookname.setUserData("name");
        bookAuthor.setUserData("author");
        bookPublusher.setUserData("publisher");
        edition.setUserData("edition");
        quantity.setUserData("qnty");
        bookSection.getItems().setAll(sections);
        requestFocus(bookId);
        requestFocus(bookname);
        requestFocus(bookAuthor);
        requestFocus(bookPublusher);
        requestFocus(edition);
        requestFocus(quantity);
    }

    private void requestFocus(TextField field) {
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    if (field.getUserData().equals("id")) {
                        bookname.requestFocus();
                    }
                    if (field.getUserData().equals("name")) {
                        bookAuthor.requestFocus();
                    }
                    if (field.getUserData().equals("author")) {
                        bookPublusher.requestFocus();
                    }
                    if (field.getUserData().equals("publisher")) {
                        edition.requestFocus();
                    }
                    if (field.getUserData().equals("edition")) {
                        quantity.requestFocus();
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (field.getUserData().equals("qnty")) {
                        edition.requestFocus();
                    }
                    if (field.getUserData().equals("edition")) {
                        bookPublusher.requestFocus();
                    }
                    if (field.getUserData().equals("publisher")) {
                        bookAuthor.requestFocus();
                    }
                    if (field.getUserData().equals("author")) {
                        bookname.requestFocus();
                    }
                    if (field.getUserData().equals("name")) {
                        bookId.requestFocus();
                    }
                }
            }
        });
    }

    private void clearFields() {
        bookId.clear();
        bookname.clear();
        bookAuthor.clear();
        bookPublusher.clear();
        edition.clear();
        quantity.clear();
        bookSection.setValue(null);
    }

    private boolean validateID() {
        Pattern p = Pattern.compile("[0-9- a-z $]+");
        Matcher m = p.matcher(bookId.getText());
        if (m.find() && m.group().equals(bookId.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "ISBN validation", "Please enter a valid book ISBN!");
            return false;
        }
    }

    private boolean checkIDBookIDAlreadyExist() {
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Book WHERE BookID = ? COLLATE NOCASE";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            pre.setString(1, bookId.getText().trim());
            rs = pre.executeQuery();
            if (rs.next()) {
                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "ISBN validation", "Book ISBN already Exist!");
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

    private boolean validateName() {
        Pattern p = Pattern.compile("[a-z A-Z # ; ' & 1-9 +]+");
        Matcher m = p.matcher(bookname.getText());
        if (m.find() && m.group().equals(bookname.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Book name validation", "Please enter a valid book name!");
            return false;
        }
    }

    private boolean validateAuthor() {
        Pattern p = Pattern.compile("[a-z A-Z ,&. -]+");
        Matcher m = p.matcher(bookAuthor.getText());
        if (m.find() && m.group().equals(bookAuthor.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Book author validation", "Please enter a valid book author!");
            return false;
        }
    }

    private boolean validatePublisher() {
        Pattern p = Pattern.compile("[a-z A-Z ,& + -]+");
        Matcher m = p.matcher(bookPublusher.getText());
        if (m.find() && m.group().equals(bookPublusher.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Book publisher validation", "Please enter a valid book publisher!");
            return false;
        }
    }

    private boolean validateEdition() {
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(edition.getText());
        if (m.find() && m.group().equals(edition.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Book edition validation", "Please enter a valid book edition!");
            return false;
        }
    }

    private boolean validateQuantity() {
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(quantity.getText());
        if (m.find() && m.group().equals(quantity.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Book quantity validation", "Please enter a valid book quantity!");
            return false;
        }
    }

    private boolean validateBookQuantity() {
        int bookQuantity = Integer.parseInt(quantity.getText());
        if (bookQuantity == 0) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Book quantity validation", "Book quantity cannot be zero!");
            return false;
        } else {
            return true;
        }
    }

    private boolean vlidateFields() {
        if (bookId.getText().isEmpty() || bookname.getText().isEmpty() || bookAuthor.getText().isEmpty() || bookPublusher.getText().isEmpty() || edition.getText().isEmpty() || quantity.getText().isEmpty() || bookSection.getValue() == null) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Field validation", "Please enter in all fields!");
            return false;
        } else {
            return true;
        }
    }

    private void updateBook(ActionEvent event) {
        PreparedStatement pre = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre3 = null;
        PreparedStatement pre4 = null;
        PreparedStatement pre21 = null;
        Connection conn = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        String query = "UPDATE Book SET Name = ?,Author = ?,Publisher = ?,Edition = ?,Quantity = ?,RemainingBooks = ?,Section = ? WHERE BookID = ?";
        String query2 = "SELECT COUNT(*) FROM IssueBook WHERE BookID = ?";
        String query21 = "SELECT COUNT(*) FROM ShortTermBook WHERE BookID = ?";
        String query3 = "UPDATE Book SET Availability = 'Available' WHERE BookID = ?";
        String query4 = "UPDATE Book SET Availability = 'Not Available' WHERE BookID = ?";
        try {
            conn = DatabaseConnection.Connect();
            String bEdition = edition.getText();
            String bQuantity = quantity.getText();
            if (validateName() && validateAuthor() && validatePublisher() && validateEdition() && validateQuantity() && validateBookQuantity()) {
                pre2 = conn.prepareStatement(query2);
                pre21 = conn.prepareStatement(query21);
                pre3 = conn.prepareStatement(query3);
                pre4 = conn.prepareStatement(query4);
                pre2.setString(1, bookId.getText());
                pre21.setString(1, bookId.getText());
                rs1 = pre2.executeQuery();
                rs2 = pre21.executeQuery();
                int issued = rs1.getInt(1);
                int shortTermIssuedBooks = rs2.getInt(1);
                int issuedBooks  = issued + shortTermIssuedBooks;
                int BookQuantity = Integer.parseInt(bQuantity);
                int BookEdition = Integer.parseInt(bEdition);
                int remaining = BookQuantity - issuedBooks;
                if (remaining == 0) {
                    pre4.setString(1, bookId.getText());
                    pre4.executeUpdate();
                }
                if (remaining > 0) {
                    pre3.setString(1, bookId.getText());
                    pre3.executeUpdate();
                }
                if (BookQuantity < issuedBooks) {
                    librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Error", "Book quantity cannot be less than issued books");
                } else {
                    pre = conn.prepareStatement(query);
                    pre.setString(1, bookname.getText().trim());
                    pre.setString(2, bookAuthor.getText().trim());
                    pre.setString(3, bookPublusher.getText().trim());
                    pre.setInt(4, BookEdition);
                    pre.setInt(5, BookQuantity);
                    pre.setInt(6, remaining);
                    pre.setString(7, bookSection.getValue());
                    pre.setString(8, bookId.getText());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText(null);
                    alert.setContentText("Save changes ?");
                    Optional<ButtonType> choice = alert.showAndWait();
                    if (choice.get() == ButtonType.OK) {
                        pre.executeUpdate();
                        Notification notification = new Notification("Message", "Book information successfully updated", 3);
                        clearFields();
                        bookId.setEditable(true);
                        isinEditMode = false;
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (pre != null) {
                    pre.close();
                }
                if (pre2 != null) {
                    pre2.close();
                }
                if (pre21 != null) {
                    pre21.close();
                }
                if (pre3 != null) {
                    pre3.close();
                }
                if (pre4 != null) {
                    pre4.close();
                }
                if (rs1 != null) {
                    rs1.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
    }

    @FXML
    private void saveBook(ActionEvent event) {
        PreparedStatement pre1 = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre3 = null;
        Connection conn = null;
        ResultSet rs = null;
        String query1 = "INSERT INTO Book (BookID,Name,Author,Publisher,Edition,Quantity,Section) VALUES (?,?,?,?,?,?,?)";
        String query2 = "SELECT Quantity FROM Book WHERE BookID = ?";
        String query3 = "UPDATE Book SET RemainingBooks = ? WHERE BookID = ?";
        if (isinEditMode) {
            if (vlidateFields() && validateID() && validateName() && validateAuthor() && validatePublisher() && validateEdition() && validateQuantity() && validateBookQuantity()) {
                updateBook(new ActionEvent());
            }
            return;
        }
        if (vlidateFields() && validateID() && checkIDBookIDAlreadyExist() && validateName() && validateAuthor() && validatePublisher() && validateEdition() && validateQuantity() && validateBookQuantity()) {
            try {
                conn = DatabaseConnection.Connect();
                pre1 = conn.prepareStatement(query1);
                pre2 = conn.prepareStatement(query2);
                pre3 = conn.prepareStatement(query3);
                String bEdition = edition.getText().trim();
                String bQuantity = quantity.getText().trim();
                pre2.setString(1, bookId.getText());
                int BookEdition = Integer.parseInt(bEdition);
                int BookQuantity = Integer.parseInt(bQuantity);
                pre1.setString(1, bookId.getText().trim());
                pre1.setString(2, bookname.getText().trim());
                pre1.setString(3, bookAuthor.getText().trim());
                pre1.setString(4, bookPublusher.getText().trim());
                pre1.setInt(5, BookEdition);
                pre1.setInt(6, BookQuantity);
                pre1.setString(7, bookSection.getValue());
                pre1.executeUpdate();
                rs = pre2.executeQuery();
                if (rs.next()) {
                    int allBooks = rs.getInt("Quantity");
                    pre3.setInt(1, allBooks);
                    pre3.setString(2, bookId.getText());
                    pre3.executeUpdate();
                    Notification notification = new Notification("Message", "Book record successfully added", 3);
                    clearFields();
                    isinEditMode = false;
                    bookId.requestFocus();
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
    private void cancelSaveBookOperation(ActionEvent event) {
        clearFields();
        bookId.setEditable(true);
        save.setDisable(false);
        isinEditMode = false;
        bookId.requestFocus();
    }

    public void handleEditAction(Book book) {
        bookId.setText(book.getBookID());
        bookname.setText(book.getBookName());
        bookAuthor.setText(book.getBookAuthor());
        bookPublusher.setText(book.getBookPublisher());
        quantity.setText("" + book.getBookQuantity());
        edition.setText("" + book.getBookEdition());
        bookSection.setValue(book.getBookSection());
    }

    public void setEditavleBookIdFieldFalse() {
        bookId.setEditable(false);
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarymanagementsystem/view/allBooks.fxml"));
        BorderPane borderPane = loader.load();
        mainController.pane.setCenter(borderPane);
        booktDataEntryController.isinEditMode = false;
    }
}

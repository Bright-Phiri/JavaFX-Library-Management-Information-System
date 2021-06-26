package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.octicons.OctIconView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import librarymanagementsystem.model.Book;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class allBooksController implements Initializable {

    @FXML
    private Label minimise;
    @FXML
    private Label fullscreen;
    @FXML
    private Label unfullscreen;
    @FXML
    private Label close;
    @FXML
    private ImageView spinner;
    @FXML
    private BorderPane boarderpane;
    @FXML
    private TextField searchTextField;
    @FXML
    private JFXButton allStudents;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> booKID;
    @FXML
    private TableColumn<Book, String> bookName;
    @FXML
    private TableColumn<Book, String> bookAuthor;
    @FXML
    private TableColumn<Book, String> bookPublisher;
    @FXML
    private TableColumn<Book, String> edition;
    @FXML
    private TableColumn<Book, Integer> quantity;
    @FXML
    private TableColumn<Book, Integer> remainingBooks;
    @FXML
    private TableColumn<Book, String> avilability;
    @FXML
    private TableColumn<Book, String> sectionCol;
    @FXML
    private Label allBooks;
    @FXML
    private Label rBooks;
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private OctIconView searchIcon;
    ObservableList<Book> data = FXCollections.observableArrayList();
    ObservableList<Book> books = FXCollections.observableArrayList();
    @FXML
    private ProgressBar prograssBar;
    @FXML
    private JFXButton importData;
    @FXML
    private CheckBox cheakall;
    @FXML
    private TableColumn<Book, CheckBox> check;

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
        initializaColumns();
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
                allBooksAndRemainingBooks();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        cheakall.selectedProperty().addListener((observable, oldValue, newValue) -> {
            books = tableView.getItems();
            for (Book book : books) {
                if (cheakall.isSelected()) {
                    book.getCheck().setSelected(true);
                } else {
                    book.getCheck().setSelected(false);
                }
            }
        });
    }

    private void initializaColumns() {
        check.setCellValueFactory(new PropertyValueFactory<>("check"));
        booKID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        bookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        bookAuthor.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        bookPublisher.setCellValueFactory(new PropertyValueFactory<>("bookPublisher"));
        edition.setCellValueFactory(new PropertyValueFactory<>("bookEdition"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("bookQuantity"));
        remainingBooks.setCellValueFactory(new PropertyValueFactory<>("remainingBooks"));
        sectionCol.setCellValueFactory(new PropertyValueFactory<>("bookSection"));
        avilability.setCellValueFactory(new PropertyValueFactory<>("availability"));
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

    private boolean validateSearchTextField() {
        if (searchTextField.getText().isEmpty()) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Field validation", "The field is empty");
            return false;
        } else {
            return true;
        }
    }

    @FXML
    private void loadBookDataentry(ActionEvent event) throws IOException {
        disableFields();
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/bookDataEntry.fxml"));
        boarderpane.setCenter(borderPane);
    }

    public void disableFields() {
        searchTextField.setVisible(false);
        allStudents.setVisible(false);
        allBooks.setVisible(false);
        importData.setVisible(false);
        rBooks.setVisible(false);
        label1.setVisible(false);
        label2.setVisible(false);
        searchIcon.setVisible(false);
    }

    private void loadData() {
        data.clear();
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Book";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                data.add(new Book(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9)));
            }
            tableView.getItems().setAll(data);
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
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    private void allBooksAndRemainingBooks() {
        Connection conn = null;
        PreparedStatement pre1 = null;
        ResultSet rs1 = null;
        PreparedStatement pre2 = null;
        ResultSet rs2 = null;
        String query1 = "SELECT SUM(Quantity) FROM Book";
        String query2 = "SELECT SUM(RemainingBooks) FROM Book";
        try {
            conn = DatabaseConnection.Connect();
            pre1 = conn.prepareStatement(query1);
            rs1 = pre1.executeQuery();
            int Books = rs1.getInt(1);
            allBooks.setText("" + Books);
            pre2 = conn.prepareStatement(query2);
            rs2 = pre2.executeQuery();
            int rbooks = rs2.getInt(1);
            this.rBooks.setText("" + rbooks);
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (rs1 != null) {
                    rs1.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (pre1 != null) {
                    pre1.close();
                }
                if (pre2 != null) {
                    pre2.close();
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
    private void DeleteBook(ActionEvent event) {
        PreparedStatement pre = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre3 = null;
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String query = "DELETE FROM Book WHERE BookID = ?";
        String query2 = "SELECT * FROM IssueBook WHERE BookID = ?";
        String query3 = "SELECT * FROM ShortTermBook WHERE BookID = ?";
        Book selectedBook = (Book) tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            Notification notification = new Notification("Information", "Select a book to delete", 5);
        } else {
            try {
                conn = DatabaseConnection.Connect();
                pre = conn.prepareStatement(query);
                pre2 = conn.prepareStatement(query2);
                pre3 = conn.prepareStatement(query3);
                pre2.setString(1, selectedBook.getBookID());
                pre3.setString(1, selectedBook.getBookID());
                rs = pre2.executeQuery();
                rs2 = pre3.executeQuery();
                if (rs.next() || rs2.next()) {
                    librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.INFORMATION, "Information", "Other students are still holding this book");
                } else {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Confirmatiin");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure you want to delete this book ?");
                    Optional<ButtonType> choice = alert.showAndWait();
                    if (choice.get() == ButtonType.OK) {
                        pre.setString(1, selectedBook.getBookID());
                        pre.executeUpdate();
                        allBooksAndRemainingBooks();
                        Notification notification = new Notification("Information", "Book successfully deleted", 3);
                        loadData();
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
                    if (pre3 != null) {
                        pre3.close();
                    }
                    if (rs != null) {
                        rs.close();
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
    }

    @FXML
    private void loadUpdateBook(ActionEvent event) throws IOException {
        String selectQuery = "SELECT * FROM Book WHERE BookID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarymanagementsystem/view/bookDataEntry.fxml"));
        Parent root = loader.load();
        Book selectedBook = (Book) tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            Notification notification = new Notification("Information", "Select a book to update", 5);
        } else {
            disableFields();
            booktDataEntryController booktDataEntryController = (booktDataEntryController) loader.getController();
            booktDataEntryController.handleEditAction(selectedBook);
            booktDataEntryController.isinEditMode = true;
            booktDataEntryController.setEditavleBookIdFieldFalse();
            boarderpane.setCenter(root);
        }
    }

    @FXML
    private void searchBook(KeyEvent event) {
        FilteredList<Book> filteredList = new FilteredList<>(data, p -> true);
        searchTextField.textProperty().addListener((ObservableValue, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Book>) book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (book.getBookID().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (book.getBookName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (book.getBookAuthor().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (book.getBookPublisher().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (book.getBookSection().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                tableView.setPlaceholder(new Text("No record match your search"));
                return false;
            });
            SortedList<Book> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.getItems().setAll(sortedList);
        });

    }

    @FXML
    private void importExcelData(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre3 = null;
        PreparedStatement pre4;
        ResultSet rs = null;
        ResultSet rs1;
        int counter = 0;
        String query2 = "SELECT Quantity FROM Book WHERE BookID = ?";
        String query3 = "UPDATE Book SET RemainingBooks = ? WHERE BookID = ?";
        String query4 = "SELECT * FROM Book WHERE BookID = ?";
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Excel file");
        chooser.setInitialDirectory(new File(System.getProperty("user.home") + "//Documents"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files", "*.xlsx"));
        FileInputStream fileInputStream;
        File file = chooser.showOpenDialog(getStage());
        if (file != null) {
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement("INSERT INTO Book (BookID,Name,Author,Publisher,Edition,Quantity,Section) VALUES (?,?,?,?,?,?,?)");
                fileInputStream = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row;
                row = sheet.getRow(0);
                if (row.getCell(0).getStringCellValue().equals("ISBN") && row.getCell(1).getStringCellValue().equals("Book Title") && row.getCell(2).getStringCellValue().equals("Book Author") && row.getCell(3).getStringCellValue().equals("Book Publisher") && row.getCell(4).getStringCellValue().equals("Edition") && row.getCell(5).getStringCellValue().equals("Quantity") && row.getCell(6).getStringCellValue().equals("Book Section")) {
                    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                        row = sheet.getRow(i);
                        pre4 = connection.prepareStatement(query4);
                        pre4.setString(1, row.getCell(0).getStringCellValue());
                        rs1 = pre4.executeQuery();
                        if (rs1.next()) {
                            Notification notification = new Notification("Information", "Book id already exists", 3);
                        } else {
                            pre2 = connection.prepareStatement(query2);
                            pre3 = connection.prepareStatement(query3);
                            pre2.setString(1, row.getCell(0).getStringCellValue());
                            preparedStatement.setString(1, row.getCell(0).getStringCellValue());
                            preparedStatement.setString(2, row.getCell(1).getStringCellValue());
                            preparedStatement.setString(3, row.getCell(2).getStringCellValue());
                            preparedStatement.setString(4, row.getCell(3).getStringCellValue());
                            preparedStatement.setInt(5, (int) row.getCell(4).getNumericCellValue());
                            preparedStatement.setInt(6, (int) row.getCell(5).getNumericCellValue());
                            preparedStatement.setString(7, row.getCell(6).getStringCellValue());
                            preparedStatement.executeUpdate();
                            rs = pre2.executeQuery();
                            if (rs.next()) {
                                int allbooks = rs.getInt("Quantity");
                                pre3.setInt(1, allbooks);
                                pre3.setString(2, row.getCell(0).getStringCellValue());
                                pre3.executeUpdate();
                                counter++;
                            }
                        }
                    }
                    if (counter == 0) {
                        Notification notification = new Notification("Information", "No records imported", 3);
                    } else {
                        String message = (counter == 1) ? counter + " record successfully imported" : counter + " records successfully imported";
                        Notification notification = new Notification("Information", message, 3);
                    }
                } else {
                    Notification notification = new Notification("Information", "Failed to import data from the file", 3);
                }
            } catch (SQLException | IOException ex) {
                Logger.getLogger(allBooksController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (pre2 != null) {
                        pre2.close();
                    }
                    if (pre3 != null) {
                        pre3.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(allBooksController.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadData();
                allBooksAndRemainingBooks();
            }
        }
    }

    private Stage getStage() {
        return (Stage) tableView.getScene().getWindow();
    }
}

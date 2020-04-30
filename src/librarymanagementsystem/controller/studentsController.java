package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;
import librarymanagementsystem.model.Student;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class studentsController implements Initializable {

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
    private TextField searchTextField;
    @FXML
    private TextField studentID;
    @FXML
    private TextField studentName;
    @FXML
    private JFXButton save;
    @FXML
    private JFXButton update;
    @FXML
    private JFXButton delete;
    @FXML
    private TextField studentEmail;
    @FXML
    private TextField studentPhone;
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, String> stuID;
    @FXML
    private TableColumn<Student, String> stunNme;
    @FXML
    private TableColumn<Student, String> stueEmail;
    @FXML
    private TableColumn<Student, String> stuPhone;
    ObservableList<Student> studentData = FXCollections.observableArrayList();
    @FXML
    private ProgressBar progressBar;
    @FXML
    private JFXButton export2;
    @FXML
    private JFXButton cancel;
    @FXML
    private ComboBox<String> comboBox;
    ContextMenu contextMenu;
    String comboboxValue;
    @FXML
    private ContextMenu selectStudentContext;
    @FXML
    private MenuItem selectMenu;
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
        initiliazeColumns();
        studentID.setUserData("id");
        studentName.setUserData("name");
        studentEmail.setUserData("email");
        studentPhone.setUserData("phone");
        contextMenu = new ContextMenu(new MenuItem("Unarchive record"));
        contextMenu.setOnAction((e) -> {
            unArchiveRecord();
        });
        comboBox.getItems().addAll(FXCollections.observableArrayList("Students", "Archieved list"));
        comboBox.setValue("Students");
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
        progressBar.progressProperty().bind(task.progressProperty());
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
        requestFocus(studentID);
        requestFocus(studentName);
        requestFocus(studentEmail);
        requestFocus(studentPhone);
    }

    private void clearFields() {
        studentID.clear();
        studentName.clear();
        studentEmail.clear();
        studentPhone.clear();
    }

    private boolean validateFields() {
        if (studentID.getText().isEmpty() || studentName.getText().isEmpty() || studentEmail.getText().isEmpty() || studentPhone.getText().isEmpty()) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "Field validation", "Please enter in all fields!");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateID() {
        Pattern p = Pattern.compile("[0-9./a-z A-Z]+");
        Matcher M = p.matcher(studentID.getText());
        if (M.find() && M.group().equals(studentID.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "ID validation", "Please enter a valid student id!");
            return false;
        }
    }

    private boolean validateName() {
        Pattern p = Pattern.compile("[a-z A-Z]+");
        Matcher M = p.matcher(studentName.getText());
        if (M.find() && M.group().equals(studentName.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "Name validation", "Please enter a valid student name!");
            return false;
        }
    }

    private boolean validateEmail() {
        Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(_A-Za-z0-9-)*@[A-Za-z0-9-].+(\\.[A-Za-z]+)*(\\.[A-Za-z]{2,})$");
        Matcher M = p.matcher(studentEmail.getText());
        if (M.find() && M.group().equals(studentEmail.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "Email validation", "Please enter a valid email address!");
            return false;
        }
    }

    private boolean validatePhoneNumber() {
        Pattern p1 = Pattern.compile("[+]{1}265[8]{2}[0-9]{7}");
        Pattern p2 = Pattern.compile("[+]{1}265[9]{2}[0-9]{7}");
        Matcher m1 = p1.matcher(studentPhone.getText());
        Matcher m2 = p2.matcher(studentPhone.getText());
        if (m1.find() && m1.group().equals(studentPhone.getText()) || m2.find() && m2.group().equals(studentPhone.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "Phone number validation", "Please enter a valid phone number!");
            return false;
        }
    }

    private boolean checkIFIDExist() {
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Student WHERE studentID = ?";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            pre.setString(1, studentID.getText());
            rs = pre.executeQuery();
            if (rs.next()) {
                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "ID validation", "Student id already exist");
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

    private void requestFocus(TextField field) {
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    if (field.getUserData().equals("id")) {
                        studentName.requestFocus();
                    }
                    if (field.getUserData().equals("name")) {
                        studentEmail.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        studentPhone.requestFocus();
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (field.getUserData().equals("phone")) {
                        studentEmail.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        studentName.requestFocus();
                    }
                    if (field.getUserData().equals("name")) {
                        studentID.requestFocus();
                    }
                }
            }
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
    private void cancel(ActionEvent event) {
        clearFields();
        delete.setVisible(false);
        update.setVisible(false);
        save.setDisable(false);
        studentID.setEditable(true);
        studentTable.getSelectionModel().clearSelection();
        searchTextField.clear();
    }

    @FXML
    private void fetchStudentFeesDetails(MouseEvent event) {
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Student WHERE studentID = ?";
        Student stu = (Student) studentTable.getSelectionModel().getSelectedItem();
        if (stu == null) {
        }
        if (stu != null) {
            if (comboBox.getValue().equals("Archieved list")) {
                save.setDisable(false);
                update.setVisible(false);
                delete.setVisible(false);
            } else {
                try {
                    studentID.setEditable(false);
                    update.setVisible(true);
                    delete.setVisible(true);
                    save.setDisable(true);
                    conn = DatabaseConnection.Connect();
                    pre = conn.prepareStatement(query);
                    pre.setString(1, stu.getStudentID());
                    rs = pre.executeQuery();
                    while (rs.next()) {
                        studentID.setText(rs.getString(1));
                        studentName.setText(rs.getString(2));
                        studentEmail.setText(rs.getString(3));
                        studentPhone.setText(rs.getString(4));
                    }
                } catch (SQLException ex) {
                    System.err.println(ex);
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (pre != null) {
                            pre.close();
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
    }

    @FXML
    private void saveStudent(ActionEvent event) {
        PreparedStatement pre = null;
        Connection conn = null;
        String query = "INSERT INTO Student (studentID,Name,Email,Phone) VALUES (?,?,?,?)";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            if (validateFields() && validateID() && checkIFIDExist() && validateName() && validateEmail() && validatePhoneNumber()) {
                pre.setString(1, studentID.getText().trim());
                pre.setString(2, studentName.getText().trim());
                pre.setString(3, studentEmail.getText().trim());
                pre.setString(4, studentPhone.getText().trim());
                pre.executeUpdate();
                loadData();
                Notification notification = new Notification("Message", "Student successfully added", 3);
                clearFields();
                save.setDisable(false);
            }
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                if (pre != null) {
                    pre.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e1) {
                System.err.println(e1);
            }
        }
    }

    @FXML
    private void updateStudent(ActionEvent event) {
        PreparedStatement pre = null;
        Connection conn = null;
        String query = "UPDATE Student SET Name = ?,Email = ?,Phone = ? WHERE studentID = ?";
        try {
            conn = DatabaseConnection.Connect();
            if (validateFields() && validateName() && validateEmail() && validatePhoneNumber()) {
                pre = conn.prepareStatement(query);
                pre.setString(1, studentName.getText().trim());
                pre.setString(2, studentEmail.getText().trim());
                pre.setString(3, studentPhone.getText().trim());
                pre.setString(4, studentID.getText().trim());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Save changes ?");
                Optional<ButtonType> choice = alert.showAndWait();
                if (choice.get() == ButtonType.OK) {
                    pre.executeUpdate();
                    loadData();
                    clearFields();
                    Notification notification = new Notification("Message", "Student information successfully updated", 3);
                    studentID.setEditable(true);
                    update.setVisible(false);
                    delete.setVisible(false);
                    save.setDisable(false);
                    searchTextField.clear();
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (pre != null) {
                    pre.close();
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
    private void deleteStudent(ActionEvent event) {
        PreparedStatement pre = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre21 = null;
        PreparedStatement pre3 = null;
        PreparedStatement pre4 = null;
        PreparedStatement insert = null;
        Connection conn = null;
        ResultSet rs2 = null;
        ResultSet rs21 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        String query = "DELETE FROM Student WHERE studentID = ?";
        String query2 = "SELECT * FROM IssueBook WHERE StudentID = ?";
        String query21 = "SELECT * FROM ShortTermBook WHERE StudentID = ?";
        String query3 = "SELECT Name FROM Student WHERE StudentID = ?";
        String select = "SELECT * FROM Student WHERE StudentId = ?";
        String insertQuery = "INSERT INTO ArhieveStudent (studentID,Name,Email,Phone) VALUES (?,?,?,?)";
        try {
            conn = DatabaseConnection.Connect();
            pre2 = conn.prepareStatement(query2);
            pre21 = conn.prepareStatement(query21);
            pre3 = conn.prepareStatement(query3);
            pre4 = conn.prepareStatement(select);
            insert = conn.prepareStatement(insertQuery);
            pre2.setString(1, studentID.getText());
            pre21.setString(1, studentID.getText());
            pre3.setString(1, studentID.getText());
            rs2 = pre2.executeQuery();
            rs21 = pre21.executeQuery();
            if (rs2.next() || rs21.next()) {
                rs3 = pre3.executeQuery();
                String stuName = rs3.getString("Name");
                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", stuName + " is holding a book");
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to archieve this student record ?");
                Optional<ButtonType> choice = alert.showAndWait();
                if (choice.get() == ButtonType.OK) {
                    pre4.setString(1, studentID.getText());
                    rs4 = pre4.executeQuery();
                    while (rs4.next()) {
                        insert.setString(1, rs4.getString(1));
                        insert.setString(2, rs4.getString(2));
                        insert.setString(3, rs4.getString(3));
                        insert.setString(4, rs4.getString(4));
                    }
                    insert.executeUpdate();
                    pre = conn.prepareStatement(query);
                    pre.setString(1, studentID.getText());
                    pre.executeUpdate();
                    loadData();
                    save.setDisable(false);
                    update.setVisible(false);
                    delete.setVisible(false);
                    studentID.setEditable(true);
                    Notification notification = new Notification("Information", "Student record successfully moved to archieved student list", 3);
                    clearFields();
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (rs21 != null) {
                    rs21.close();
                }
                if (pre != null) {
                    pre.close();
                }
                if (pre2 != null) {
                    pre2.close();
                }
                if (pre21 != null) {
                    pre21.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (pre4 != null) {
                    pre4.close();
                }
                if (insert != null) {
                    insert.close();
                }
                if (rs4 != null) {
                    rs4.close();
                }
                if (rs3 != null) {
                    rs3.close();
                }
                if (pre3 != null) {
                    pre3.close();
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
    private void searchStudentDeatails(KeyEvent event) {
        FilteredList<Student> filteredList = new FilteredList<>(studentData, p -> true);
        searchTextField.textProperty().addListener((ObservableValue, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Student>) student -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filterLowerCase = newValue.toLowerCase();
                if (student.getStudentID().toLowerCase().contains(filterLowerCase)) {
                    return true;
                }
                if (student.getStudentName().toLowerCase().contains(filterLowerCase)) {
                    return true;
                }
                studentTable.setPlaceholder(new Text("No record match your search"));
                return false;
            });
            SortedList<Student> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(studentTable.comparatorProperty());
            studentTable.getItems().setAll(sortedList);
        });
    }

    private void loadData() {
        studentData.clear();
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Student order by studentID";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                studentData.add(new Student(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
            studentTable.getItems().setAll(studentData);
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pre != null) {
                    pre.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
    }

    private void initiliazeColumns() {
        stuID.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        stunNme.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        stueEmail.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));
        stuPhone.setCellValueFactory(new PropertyValueFactory<>("studentPhone"));
    }

    private void loadArchievedRecord() {
        studentData.clear();
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM ArhieveStudent";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                studentData.add(new Student(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
            studentTable.getItems().setAll(studentData);
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pre != null) {
                    pre.close();
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
    private void fetchStudentWithKey(KeyEvent event) {
        if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            fetchStudentFeesDetails(null);
        }
    }

    @FXML
    private void importData(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSet = null;
        int counter = 0;
        String query = "INSERT INTO Student (studentID,Name,Email,Phone) VALUES (?,?,?,?)";
        String query2 = "SELECT * FROM Student WHERE studentID = ?";
        File file;
        FileInputStream fileInputStream;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Data");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Documents"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel file", "*.xlsx"));
        file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement2 = connection.prepareStatement(query2);
                fileInputStream = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row;
                row = sheet.getRow(0);
                if (row.getCell(0).getStringCellValue().equals("Student ID") && row.getCell(1).getStringCellValue().equals("Student Name") && row.getCell(2).getStringCellValue().equals("Email") && row.getCell(3).getStringCellValue().equals("Contact")) {
                    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                        row = sheet.getRow(i);
                        preparedStatement2.setString(1, row.getCell(0).getStringCellValue());
                        resultSet = preparedStatement2.executeQuery();
                        if (resultSet.next()) {
                            Notification notification = new Notification("Information", "Student id already exists", 3);
                        } else {
                            preparedStatement.setString(1, row.getCell(0).getStringCellValue());
                            preparedStatement.setString(2, row.getCell(1).getStringCellValue());
                            preparedStatement.setString(3, row.getCell(2).getStringCellValue());
                            preparedStatement.setString(4, row.getCell(3).getStringCellValue());
                            preparedStatement.executeUpdate();
                            counter++;
                        }
                    }
                    if (counter == 0) {
                        Notification notification = new Notification("Information", "No records imported", 3);
                        cancel(new ActionEvent());
                    } else {
                        String message = (counter == 1) ? counter + " record successfully imported" : counter + " records successfully imported";
                        Notification notification = new Notification("Information", message, 3);
                    }
                } else {
                    Notification notification = new Notification("Information", "Failed to import data from the file", 3);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (preparedStatement2 != null) {
                        preparedStatement2.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadData();
                cancel(new ActionEvent());
            }
        }
    }

    private Stage getStage() {
        return (Stage) studentID.getScene().getWindow();
    }

    @FXML
    private void requestMenu(ContextMenuEvent event) {
        if (comboBox.getValue().equals("Archieved list")) {
            contextMenu.show(studentTable.getScene().getWindow(), event.getSceneX() + 90, event.getSceneY() + 40);
        }
    }

    @FXML
    private void selectRecordsType(ActionEvent event) {
        if (comboBox.getValue().equals("Students")) {
            selectMenu.setVisible(true);
            loadData();
        }
        if (comboBox.getValue().equals("Archieved list")) {
            selectMenu.setVisible(false);
            loadArchievedRecord();
            cancel(new ActionEvent());
        }
    }

    private void unArchiveRecord() {
        String insert = "INSERT INTO Student (studentID,Name,Email,Phone) VALUES (?,?,?,?)";
        String deleteQuery = "DELETE FROM ArhieveStudent WHERE studentID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        Student student = (Student) studentTable.getSelectionModel().getSelectedItem();
        if (student == null) {
            Notification notification = new Notification("Information", "No record selected", 3);
        }
        if (student != null) {
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement(insert);
                preparedStatement1 = connection.prepareStatement(deleteQuery);
                preparedStatement.setString(1, student.getStudentID());
                preparedStatement.setString(2, student.getStudentName());
                preparedStatement.setString(3, student.getStudentEmail());
                preparedStatement.setString(4, student.getStudentPhone());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Unarhieve record");
                alert.setContentText("Are you sure you want to unarchieve " + student.getStudentName() + " record ?");
                alert.setHeaderText(null);
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    preparedStatement1.setString(1, student.getStudentID());
                    preparedStatement.executeUpdate();
                    preparedStatement1.executeUpdate();
                    Notification notification = new Notification("Information", "Student record successfully moved to archieved student records", 3);
                }
            } catch (SQLException ex) {
                Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
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
                    Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadArchievedRecord();
            }
        }
    }

    @FXML
    private void deleteStudentRecord(ActionEvent event) {
        String query = "DELETE FROM Student WHERE studentID = ?";
        String query2 = "SELECT * FROM IssueBook WHERE StudentID = ?";
        String query21 = "SELECT * FROM ShortTermBook WHERE StudentID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement2 = null;
        PreparedStatement preparedStatement3 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        Student student = (Student) studentTable.getSelectionModel().getSelectedItem();
        if (student == null) {
            Notification notification = new Notification("Information", "No record selected", 3);
        } else {
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement3 = connection.prepareStatement(query21);
                preparedStatement.setString(1, student.getStudentID());
                preparedStatement2.setString(1, student.getStudentID());
                preparedStatement3.setString(1, student.getStudentID());
                rs = preparedStatement2.executeQuery();
                rs1 = preparedStatement3.executeQuery();
                if (rs.next() || rs1.next()) {
                    librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", student.getStudentName() + " is holding a book");
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete record");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure you want delete " + student.getStudentName() + " record ?");
                    Optional<ButtonType> optional = alert.showAndWait();
                    if (optional.get() == ButtonType.OK) {
                        preparedStatement.executeUpdate();
                        Notification notification = new Notification("Information", "Student record successfully deleted", 3);
                        cancel(new ActionEvent());
                    } else{
                        cancel(new ActionEvent());
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (rs1 != null) {
                        rs1.close();
                    }
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (preparedStatement2 != null) {
                        preparedStatement2.close();
                    }
                    if (preparedStatement3 != null) {
                        preparedStatement3.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadData();
            }
        }
    }
}

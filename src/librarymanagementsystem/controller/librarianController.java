/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Librarian;
import librarymanagementsystem.model.Notification;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class librarianController implements Initializable {

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
    private ProgressBar progressBar;
    @FXML
    private JFXButton cancel;
    @FXML
    private JFXButton save;
    @FXML
    private JFXButton importData;
    @FXML
    private TableView<Librarian> librarianTable;
    @FXML
    private TableColumn<Librarian, String> firstNameCol;
    @FXML
    private TableColumn<Librarian, String> lastNameCol;
    @FXML
    private TableColumn<Librarian, String> userNameCol;
    @FXML
    private TableColumn<Librarian, String> emailAddressCol;
    @FXML
    private TableColumn<Librarian, String> passwordCol;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField userName;
    @FXML
    private TextField emailAddress;
    @FXML
    private PasswordField password1;
    @FXML
    private PasswordField password2;
    ObservableList<Librarian> data = FXCollections.observableArrayList();
    public static boolean isEditableMode = false;
    private int id;

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
        password1.setTooltip(new Tooltip("The password must be at least 8 characters long"));
        password2.setTooltip(new Tooltip("The password must be at least 8 characters long"));
        initializeColumns();
        setTextFieldUserData();
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
        firstName.setUserData("firstName");
        lastName.setUserData("lastName");
        userName.setUserData("username");
        emailAddress.setUserData("email");
        password1.setUserData("pass1");
        password2.setUserData("pass2");
        requestFocus(firstName);
        requestFocus(lastName);
        requestFocus(userName);
        requestFocus(emailAddress);
        requestFocus(password1);
        requestFocus(password2);
    }

    private void requestFocus(TextField field) {
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    if (field.getUserData().equals("firstName")) {
                        lastName.requestFocus();
                    }
                    if (field.getUserData().equals("lastName")) {
                        userName.requestFocus();
                    }
                    if (field.getUserData().equals("username")) {
                        emailAddress.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        password1.requestFocus();
                    }
                    if (field.getUserData().equals("pass1")) {
                        password2.requestFocus();
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (field.getUserData().equals("pass2")) {
                        password1.requestFocus();
                    }
                    if (field.getUserData().equals("pass1")) {
                        emailAddress.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        userName.requestFocus();
                    }
                    if (field.getUserData().equals("username")) {
                        lastName.requestFocus();
                    }
                    if (field.getUserData().equals("lastName")) {
                        firstName.requestFocus();
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
    }

    @FXML
    private void searchLibrarianDeatails(KeyEvent event) {
        FilteredList<Librarian> filteredList = new FilteredList<>(data, p -> true);
        searchTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Librarian>) librarian -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filterToLowerCase = newValue.toLowerCase();
                if (librarian.getFirstName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (librarian.getLastName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (librarian.getUserName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (librarian.getEmailAddress().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                librarianTable.setPlaceholder(new Text("No record match your search"));
                return false;
            });
            SortedList<Librarian> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(librarianTable.comparatorProperty());
            librarianTable.setItems(sortedList);
        }));
    }

    @FXML
    private void importDataFromExcel(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSet = null;
        int counter = 0;
        String query = "INSERT INTO User (Firstname, Lastame,Username, Email,Password,Usertype) VALUES (?,?,?,?,?,?)";
        String query1 = "SELECT * FROM User WHERE Username = ?";
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
                preparedStatement2 = connection.prepareStatement(query1);
                fileInputStream = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row;
                row = sheet.getRow(0);
                if (row.getCell(0).getStringCellValue().equals("First Name") && row.getCell(1).getStringCellValue().equals("Last Name") && row.getCell(2).getStringCellValue().equals("Username") && row.getCell(3).getStringCellValue().equals("Email Address")) {
                    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                        row = sheet.getRow(i);
                        preparedStatement2.setString(1, row.getCell(2).getStringCellValue());
                        resultSet = preparedStatement2.executeQuery();
                        if (resultSet.next()) {
                            Notification notification = new Notification("Information", "Username already exists", 3);
                        } else {
                            preparedStatement.setString(1, row.getCell(0).getStringCellValue());
                            preparedStatement.setString(2, row.getCell(1).getStringCellValue());
                            preparedStatement.setString(3, row.getCell(2).getStringCellValue());
                            preparedStatement.setString(4, row.getCell(3).getStringCellValue());
                            preparedStatement.setString(5, row.getCell(4).getStringCellValue());
                            preparedStatement.setString(6, "Librarian");
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
                        cancel(new ActionEvent());
                    }
                } else {
                    Notification notification = new Notification("Error", "Failed to import data from the file", 3);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadData();
            }
        }
    }

    private boolean validateName(TextField field) {
        Pattern p = Pattern.compile("[a-z A-Z]+");
        Matcher M = p.matcher(field.getText());
        if (M.find() && M.group().equals(field.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "Name validation", field.getUserData() + " is invalid");
            return false;
        }
    }

    private boolean validateEmail() {
        Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(_A-Za-z0-9-)*@[A-Za-z0-9-]+(\\.[A-Za-z]+)*(\\.[A-Za-z]{2,})$");
        Matcher M = p.matcher(emailAddress.getText());
        if (M.find() && M.group().equals(emailAddress.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "Email validation", "Please enter a valid email address!");
            return false;
        }
    }

    private boolean validateFields() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || userName.getText().isEmpty() || emailAddress.getText().isEmpty() || password1.getText().isEmpty() || password2.getText().isEmpty()) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "Fields validation", "Please enter in all fields!");
            return false;
        }
        return true;
    }

    @FXML
    private void saveLibrarian(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String query = "INSERT INTO User (Firstname, Lastame,Username, Email,Password,Usertype) VALUES (?,?,?,?,?,?)";
        String update = "UPDATE User SET Firstname = ?, Lastame = ?,Username = ?, Email = ?,Password = ? WHERE ID = ?";
        if (validateFields() && validateName(firstName) && validateName(lastName) && validateName(userName) && validateEmail() && validatePasswordLength()) {
            try {
                if (isEditableMode) {
                    connection = DatabaseConnection.Connect();
                    preparedStatement = connection.prepareStatement(update);
                    if (validatePasswords(password1, password2)) {
                        preparedStatement.setString(1, firstName.getText());
                        preparedStatement.setString(2, lastName.getText());
                        preparedStatement.setString(3, userName.getText());
                        preparedStatement.setString(4, emailAddress.getText());
                        preparedStatement.setString(5, password1.getText());
                        preparedStatement.setInt(6, id);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText(null);
                        alert.setContentText("Save changes ?");
                        Optional<ButtonType> optional = alert.showAndWait();
                        if (optional.get() == ButtonType.OK) {
                            preparedStatement.executeUpdate();
                            Notification notification = new Notification("Information", "Librarian record successfully updated", 3);
                            clearFields();
                            firstName.requestFocus();
                            id = 0;
                            isEditableMode = false;
                        }
                    }
                } else {
                    connection = DatabaseConnection.Connect();
                    preparedStatement = connection.prepareStatement(query);
                    if (checkIfUsernameExists(userName.getText()) && validatePasswords(password1, password2)) {
                        preparedStatement.setString(1, firstName.getText());
                        preparedStatement.setString(2, lastName.getText());
                        preparedStatement.setString(3, userName.getText());
                        preparedStatement.setString(4, emailAddress.getText());
                        preparedStatement.setString(5, password1.getText());
                        preparedStatement.setString(6, "Librarian");
                        preparedStatement.executeUpdate();
                        Notification notification = new Notification("Information", "Librarian record successfully added", 3);
                        clearFields();
                        firstName.requestFocus();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadData();
            }
        }
    }

    private void clearFields() {
        firstName.clear();
        lastName.clear();
        userName.clear();
        emailAddress.clear();
        password1.clear();
        password2.clear();
    }

    private void loadData() {
        data.clear();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM User WHERE Usertype = ?";
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "Librarian");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                data.add(new Librarian(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), DigestUtils.shaHex(resultSet.getString("Password"))));
            }
            librarianTable.setItems(data);
        } catch (SQLException ex) {
            Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void initializeColumns() {
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        emailAddressCol.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
    }

    @FXML
    private void loadDetailsToUpdate(ActionEvent event) {
        Librarian librarian = (Librarian) librarianTable.getSelectionModel().getSelectedItem();
        if (librarian == null) {
            Notification notification = new Notification("Information", "Select librarian record to update", 3);
        } else {
            isEditableMode = true;
            userName.setEditable(false);
            id = librarian.getId();
            firstName.setText(librarian.getFirstName());
            lastName.setText(librarian.getLastName());
            userName.setText(librarian.getUserName());
            emailAddress.setText(librarian.getEmailAddress());
            password1.setText(librarian.getPassword());
            password2.setText(librarian.getPassword());
        }
    }

    @FXML
    private void deleteLibrarian(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Librarian librarian = (Librarian) librarianTable.getSelectionModel().getSelectedItem();
        if (librarian == null) {
            Notification notification = new Notification("Information", "Select librarian record to delete", 3);
        } else {
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement("DELETE FROM User WHERE ID = ?");
                preparedStatement.setInt(1, librarian.getId());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confimation");
                alert.setContentText("Are you sure you want to delete " + librarian.getFirstName() + " " + librarian.getLastName() + " ?");
                alert.setHeaderText(null);
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    preparedStatement.executeUpdate();
                    Notification notification = new Notification("Information", "Libraian record successfully deleted", 3);
                    clearFields();
                    firstName.requestFocus();
                }
            } catch (SQLException ex) {
                Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadData();
            }
        }
    }

    private void setTextFieldUserData() {
        firstName.setUserData("First Name");
        lastName.setUserData("Last name");
        userName.setUserData("Username");
    }

    private Stage getStage() {
        return (Stage) firstName.getScene().getWindow();
    }

    private boolean checkIfUsernameExists(String username) {
        String query = "SELECT * FROM User WHERE Username = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Username Validation", "Another Librarian is using this username");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(librarianController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    private boolean validatePasswords(PasswordField password1, PasswordField password2) {
        if (password1.getText().equals(password2.getText())) {
            return true;
        }
        librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "Information", "Passwords don't match.");
        password1.clear();
        password2.clear();
        password1.requestFocus();
        return false;
    }

    private boolean validatePasswordLength() {
        if (password1.getText().length() < 8 || password2.getText().length() < 8) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Password length validation", "The password must be at least 8 characters long");
            return false;
        }
        return true;
    }
}

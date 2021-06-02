package librarymanagementsystem.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import librarymanagementsystem.model.Alert;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.LoadStage;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class createAccountController implements Initializable {

    @FXML
    private Label close;
    @FXML
    private Label minimize;
    @FXML
    private TextField username;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label word;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TranslateTransition moveText = new TranslateTransition(Duration.seconds(1), word);
        moveText.setToY(-20);
        moveText.setRate(1.5);
        moveText.play();
        username.setUserData("username");
        email.setUserData("email");
        password.setUserData("pass1");
        confirmPassword.setUserData("pass2");
        requestFocus(username);
        requestFocus(email);
        requestFocus(password);
        requestFocus(confirmPassword);
        password.setTooltip(new Tooltip("The password must be at least 8 characters long"));
        confirmPassword.setTooltip(new Tooltip("The password must be at least 8 characters long"));
    }

    @FXML
    private void close(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private boolean validateEmail() {
        Pattern p = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        Matcher M = p.matcher(email.getText());
        if (M.find() && M.group().equals(email.getText())) {
            return true;
        } else {
            Alert alert = new Alert(AlertType.ERROR, "Email validation", "Please enter a valid email address");
            return false;
        }
    }

    private boolean validateFields() {
        if (username.getText().isEmpty() || email.getText().isEmpty() || password.getText().isEmpty() || confirmPassword.getText().isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION, "Fields validation", "Please enter in all fields!");
            return false;
        }
        return true;
    }

    private boolean checkIfAccountAlreadyExist() {
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query2 = "SELECT * FROM User";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query2);
            rs = pre.executeQuery();
            if (rs.next()) {
                Alert alert = new Alert(AlertType.INFORMATION, "Information", "You can't create an account without the administrator's permission");
                username.clear();
                email.clear();
                password.clear();
                confirmPassword.clear();
                return false;
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
        return true;
    }

    @FXML
    private void createAccount(ActionEvent event) throws IOException {
        Connection conn = null;
        PreparedStatement pre = null;
        String query1 = "INSERT INTO User (Username,Email,Password,Usertype) VALUES (?,?,?,?)";
        if (validateFields() && validateEmail() && validatePasswordLength() && checkIfAccountAlreadyExist()) {
            try {
                conn = DatabaseConnection.Connect();
                pre = conn.prepareStatement(query1);
                if (password.getText().equals(confirmPassword.getText())) {
                    pre.setString(1, username.getText().trim());
                    pre.setString(2, email.getText().trim());
                    pre.setString(3, password.getText());
                    pre.setString(4, "Administrator");
                    pre.executeUpdate();
                    Notification notification = new Notification("Information", "Account successfully created", 3);
                    LoadStage stage = new LoadStage("/librarymanagementsystem/view/login.fxml", close);
                } else {
                    Alert alert = new Alert(AlertType.INFORMATION, "Information", "Passwords do not match");
                    password.clear();
                    confirmPassword.clear();
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
    }

    @FXML
    private void caancel(ActionEvent event) throws IOException {
        LoadStage stage = new LoadStage("/librarymanagementsystem/view/login.fxml", password);
    }

    private void requestFocus(TextField field) {
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    if (field.getUserData().equals("username")) {
                        email.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        password.requestFocus();
                    }
                    if (field.getUserData().equals("pass1")) {
                        confirmPassword.requestFocus();
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (field.getUserData().equals("pass2")) {
                        password.requestFocus();
                    }
                    if (field.getUserData().equals("pass1")) {
                        email.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        username.requestFocus();
                    }
                }
            }
        });
    }

    private boolean validatePasswordLength() {
        if (password.getText().length() < 8 || confirmPassword.getText().length() < 8) {
            Alert alert = new Alert(AlertType.ERROR, "Password length validation", "The password must be at least 8 characters long");
            return false;
        }
        return true;
    }
}

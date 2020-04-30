package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.LoadStage;
import librarymanagementsystem.model.Alert;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class loginController implements Initializable {

    @FXML
    private Label userLogin;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Label welcome;
    @FXML
    private TextField passwordTextField;
    @FXML
    private JFXButton login;
    @FXML
    private Label username;
    @FXML
    private Label password;
    @FXML
    private Label close;
    @FXML
    private Label minimize;
    @FXML
    private AnchorPane parentroot;
    public static int userID;
    public static String userName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tooltip closeApp = new Tooltip("Close");
        closeApp.setStyle("-fx-font-size:11");
        closeApp.setMinSize(20, 20);
        close.setTooltip(closeApp);
        Tooltip minimizeApp = new Tooltip("Minimize");
        minimizeApp.setStyle("-fx-font-size:11");
        minimizeApp.setMinSize(20, 20);
        minimize.setTooltip(minimizeApp);
        TranslateTransition translateWelcomeText = new TranslateTransition(Duration.millis(1500), welcome);
        translateWelcomeText.setToX(50);
        TranslateTransition translateuserLogin = new TranslateTransition(Duration.millis(1500), userLogin);
        translateuserLogin.setToX(-93);
        TranslateTransition translateUsernameLabel = new TranslateTransition(Duration.millis(1500), username);
        translateUsernameLabel.setToX(30);
        TranslateTransition translateUsernameField = new TranslateTransition(Duration.millis(1500), usernameTextField);
        translateUsernameField.setToX(27);
        TranslateTransition translatePasswordLabel = new TranslateTransition(Duration.millis(1500), password);
        translatePasswordLabel.setToX(30);
        TranslateTransition translatePasswordField = new TranslateTransition(Duration.millis(1500), passwordTextField);
        translatePasswordField.setToX(27);
        TranslateTransition translateButton = new TranslateTransition(Duration.millis(1500), login);
        translateButton.setToX(-48);
        FadeTransition fadeParentRoot = new FadeTransition(Duration.seconds(2), parentroot);
        fadeParentRoot.setFromValue(0);
        fadeParentRoot.setToValue(1);
        ParallelTransition paralllTransition = new ParallelTransition();
        paralllTransition.getChildren().addAll(fadeParentRoot, translateWelcomeText, translateuserLogin, translateUsernameLabel, translateUsernameField, translatePasswordLabel, translatePasswordField, translateButton);
        paralllTransition.play();
        requestFocus(usernameTextField);
        requestFocus(passwordTextField);
    }

    private boolean validateFields() {
        if (usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION, "Fields validation", "Please, enter all the required fields");
            return false;
        } else {
            return true;
        }
    }

    private void clearFields() {
        usernameTextField.clear();
        passwordTextField.clear();
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

    @FXML
    private void login(ActionEvent event) throws IOException {
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM User WHERE Username = ? AND Password = ?";
        if (validateFields() && checkIfAccountExist()) {
            try {
                conn = DatabaseConnection.Connect();
                pre = conn.prepareStatement(query);
                pre.setString(1, usernameTextField.getText().trim());
                pre.setString(2, passwordTextField.getText());
                rs = pre.executeQuery();
                if (rs.next()) {
                    switch (rs.getString("Usertype")) {
                        case "Administrator": {
                            Alert alert = new Alert(AlertType.INFORMATION, "Message", "Access granted");
                            LoadStage loadStage = new LoadStage("/librarymanagementsystem/view/adminPanel.fxml", close);
                            Thread thread = new Thread(new showMessage(rs.getString(4)));
                            thread.setDaemon(true);
                            thread.start();
                            break;
                        }
                        case "Librarian": {
                            userID = rs.getInt(1);
                            userName = rs.getString(4);
                            Alert alert = new Alert(AlertType.INFORMATION, "Message", "Access granted");
                            LoadStage loadStage = new LoadStage("/librarymanagementsystem/view/main.fxml", close);
                            Thread thread = new Thread(new showMessage(rs.getString(4)));
                            thread.setDaemon(true);
                            thread.start();
                            break;
                        }
                    }
                } else {
                    Alert alert = new Alert(AlertType.INFORMATION, "Information", "Username or password is not correct");
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

    @FXML
    private void loadPasswordRetrivalPanel(MouseEvent event) throws IOException {
        LoadStage stage = new LoadStage("/librarymanagementsystem/view/forgetPassword.fxml", close);
    }

    @FXML
    private void createAccount(MouseEvent event) throws IOException {
        LoadStage stage = new LoadStage("/librarymanagementsystem/view/createAccount.fxml", close);
    }

    @FXML
    private void signIn(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            login(new ActionEvent());
        }
    }

    private boolean checkIfAccountExist() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String selectQuery = "SELECT COUNT(*) FROM User";
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(selectQuery);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = resultSet.getInt(1);
            if (numberOfRows == 0) {
                Alert alert = new Alert(AlertType.INFORMATION, "Information", "There is no user account, create a new account");
                clearFields();
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(loginController.class.getName()).log(Level.SEVERE, null, ex);
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
                System.err.println(ex);
            }
        }
        return true;
    }

    private class updateFeeAfterADay implements Runnable {

        viewIssuedBooksController vibc = new viewIssuedBooksController();
        LocalTime localTime = LocalTime.MAX;
        LocalTime time = LocalTime.now();
        long minutes = ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.MAX);
        long restingTime = minutes * 60000;

        @Override
        public void run() {
            try {
                System.out.println(minutes);
                while (true) {
                    Thread.sleep(restingTime);
                    vibc.updateFee();
                    long m = ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.MAX);
                    if (m == 0) {
                        long m2 = ChronoUnit.MINUTES.between(LocalTime.now().plusMinutes(1), LocalTime.MAX);
                        restingTime = m2 * 60000;
                        System.out.println(m2);
                    } else {
                        restingTime = m * 60000;
                        System.out.println(m);
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(loginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class showMessage extends Task<Void> {
        private final String username;
        public showMessage(String username){
               this.username = username;
        }

        @Override
        protected Void call() throws Exception {
            Thread.sleep(1500);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Notification notification = new Notification("Message", "Welcome "+username, 3);
                }
            });
            return null;
        }
    }

    private void requestFocus(TextField field) {
        field.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    passwordTextField.requestFocus();
                }
                if (event.getCode() == KeyCode.UP) {
                    usernameTextField.requestFocus();
                }
            }
        });
    }
}

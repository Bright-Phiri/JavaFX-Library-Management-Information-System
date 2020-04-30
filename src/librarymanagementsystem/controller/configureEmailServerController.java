/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class configureEmailServerController implements Initializable {

    @FXML
    private TextField seerverName;
    @FXML
    private TextField serverPort;
    @FXML
    private TextField systemEmail;
    @FXML
    private PasswordField emailPassword;
    @FXML
    private CheckBox authentication;
    @FXML
    private CheckBox tls;
    @FXML
    private JFXButton cancel;
    @FXML
    private JFXButton save;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        authentication.setUserData("auth");
        tls.setUserData("tls");
        loadMailServerDetails();
        seerverName.setUserData("name");
        serverPort.setUserData("port");
        systemEmail.setUserData("email");
        emailPassword.setUserData("password");
        requestFocus(seerverName);
        requestFocus(serverPort);
        requestFocus(systemEmail);
        requestFocus(emailPassword);
    }

    @FXML
    private void cancelOperation(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void saveMailServerProperties(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement2 = null;
        String query = "INSERT INTO MailServer VALUES (?,?,?,?,?,?)";
        String query2 = "DELETE FROM MailServer";
        if (validateFields() && validateEmail() && validateCheckBoxes(authentication) && validateCheckBoxes(tls)) {
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement.setString(1, seerverName.getText());
                preparedStatement.setInt(2, Integer.parseInt(serverPort.getText()));
                preparedStatement.setString(3, systemEmail.getText());
                preparedStatement.setString(4, emailPassword.getText());
                preparedStatement.setString(5, "true");
                preparedStatement.setString(6, "true");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Save Changes ?");
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    preparedStatement2.executeUpdate();
                    preparedStatement.executeUpdate();
                    Notification notification = new Notification("Information", "Email Server successfully configured", 3);
                }
            } catch (SQLException ex) {
                Logger.getLogger(configureEmailServerController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
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
                    Logger.getLogger(configureEmailServerController.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadMailServerDetails();
            }
        }
    }

    private boolean validateEmail() {
        Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(_A-Za-z0-9-)*@[A-Za-z0-9-]+(\\.[A-Za-z]+)*(\\.[A-Za-z]{2,})$");
        Matcher m = p.matcher(systemEmail.getText());
        if (m.find() && m.group().equals(systemEmail.getText())) {
            return true;
        }
        librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.ERROR, "Email Validation", "Email is invalid");
        return false;
    }

    private boolean validateFields() {
        if (seerverName.getText().isEmpty() || serverPort.getText().isEmpty() || systemEmail.getText().isEmpty() || emailPassword.getText().isEmpty()) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Fields Validation", "Please enter in all fields");
            return false;
        }
        return true;
    }

    private boolean validateCheckBoxes(CheckBox checkBox) {
        if (!checkBox.isSelected()) {
            if (checkBox.getUserData().equals("auth")) {
                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Checkbox Validation", "Enable Authentication to allow secure connection");
            }
            if (checkBox.getUserData().equals("tls")) {
                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Checkbox Validation", "Enable Transport Layer Security Protocol to allow message encryption");
            }
            return false;
        }
        return true;
    }

    private void loadMailServerDetails() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM MailServer";
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                seerverName.setText(resultSet.getString(1));
                serverPort.setText(String.valueOf(resultSet.getInt(2)));
                systemEmail.setText(resultSet.getString(3));
                emailPassword.setText(resultSet.getString(4));
                authentication.setSelected(Boolean.valueOf(resultSet.getString(5)));
                tls.setSelected(Boolean.valueOf(resultSet.getString(6)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(configureEmailServerController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(configureEmailServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void clearFields() {
        seerverName.clear();
        serverPort.clear();
        systemEmail.clear();
        emailPassword.clear();
        authentication.setSelected(false);
        tls.setSelected(false);
        loadMailServerDetails();
    }

    private void requestFocus(TextField field) {
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    if (field.getUserData().equals("name")) {
                        serverPort.requestFocus();
                    }
                    if (field.getUserData().equals("port")) {
                        systemEmail.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        emailPassword.requestFocus();
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (field.getUserData().equals("password")) {
                        systemEmail.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        serverPort.requestFocus();
                    }
                    if (field.getUserData().equals("port")) {
                        seerverName.requestFocus();
                    }
                }
            }
        });
    }
}

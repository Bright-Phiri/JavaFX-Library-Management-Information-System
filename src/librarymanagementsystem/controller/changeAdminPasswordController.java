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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class changeAdminPasswordController implements Initializable {

    @FXML
    private JFXButton change;
    @FXML
    private PasswordField currentPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField newpassword2;
    @FXML
    private JFXButton cancel;
    @FXML
    private JFXButton save;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentPassword.setUserData("password1");
        newPassword.setUserData("password2");
        newpassword2.setUserData("password3");
        requestFocus(newPassword);
        requestFocus(newpassword2);
        requestFocus(currentPassword);
        newPassword.setTooltip(new Tooltip("The password must be at least 8 characters long"));
        newpassword2.setTooltip(new Tooltip("The password must be at least 8 characters long"));
    }

    @FXML
    private void changePassword(ActionEvent event) {
        Connection conn = null;
        PreparedStatement pre1 = null;
        PreparedStatement pre2 = null;
        ResultSet rs = null;
        String query1 = "SELECT * FROM User WHERE Usertype = ? AND Password = ?";
        String query2 = "UPDATE User SET Password = ? WHERE Usertype = ?";
        if (validateFields() && validatePasswordLength()) {
            try {
                conn = DatabaseConnection.Connect();
                pre1 = conn.prepareStatement(query1);
                pre2 = conn.prepareStatement(query2);
                pre1.setString(1, "Administrator");
                pre1.setString(2, currentPassword.getText());
                rs = pre1.executeQuery();
                if (rs.next()) {
                    if (newPassword.getText().equals(newpassword2.getText())) {
                        pre2.setString(1, newpassword2.getText());
                        pre2.setString(2, "Administrator");
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText(null);
                        alert.setContentText("Save changes ?");
                        Optional<ButtonType> choice = alert.showAndWait();
                        if (choice.get() == ButtonType.OK) {
                            pre2.executeUpdate();
                            Notification notification = new Notification("Information", "Password changed", 3);
                            change.setDisable(false);
                            clearFields();
                            disablePasswordFieldsandButtons();
                        }
                    } else {
                        librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", "Passwords don't match");
                        newPassword.clear();
                        newpassword2.clear();
                    }
                } else {
                    librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Information", "Current password is not correct");
                }
            } catch (SQLException ex) {
                System.err.println(ex);
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
    private void enablePasswordFieldsandButtons(ActionEvent event) {
        change.setDisable(true);
        currentPassword.setEditable(true);
        newPassword.setEditable(true);
        newpassword2.setEditable(true);
        save.setDisable(false);
        cancel.setDisable(false);
    }

    private boolean validateFields() {
        if (currentPassword.getText().isEmpty() || newPassword.getText().isEmpty() || newpassword2.getText().isEmpty()) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Fields validation", "Please enter in all fields");
            return false;
        } else {
            return true;
        }
    }

    private void disablePasswordFieldsandButtons() {
        currentPassword.setEditable(false);
        newPassword.setEditable(false);
        newpassword2.setEditable(false);
        save.setDisable(true);
        cancel.setDisable(true);
    }

    private void clearFields() {
        currentPassword.clear();
        newPassword.clear();
        newpassword2.clear();
    }

    @FXML
    private void cancelOperation(ActionEvent event) {
        change.setDisable(false);
        clearFields();
        disablePasswordFieldsandButtons();
    }

    private boolean validatePasswordLength() {
        if (newPassword.getText().length() < 8 || newpassword2.getText().length() < 8) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Password length validation", "The password must be at least 8 characters long");
            return false;
        }
        return true;
    }

    private void requestFocus(TextField field) {
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    if (field.getUserData().equals("password1")) {
                        newPassword.requestFocus();
                    }
                    if (field.getUserData().equals("password2")) {
                        newpassword2.requestFocus();
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (field.getUserData().equals("password3")) {
                        newPassword.requestFocus();
                    }
                    if (field.getUserData().equals("password2")) {
                        currentPassword.requestFocus();
                    }
                }
            }
        });
    }
}

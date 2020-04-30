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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;

public class changeFeePerDayController implements Initializable {

    @FXML
    private TextField currentFee;
    @FXML
    private TextField newFee;
    @FXML
    private JFXButton update;
    @FXML
    private JFXButton cancel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fetchCurrentFee();
    }

    @FXML
    private void changeFeePerDay(ActionEvent event) {
        String updateQuery = "UPDATE Account SET LateFeePerDay = ? WHERE ID = 1";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        if (validateField() && validateLateFee()) {
            double lFee = Double.parseDouble(newFee.getText());
            if (lFee <= 0) {
                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.INFORMATION, "Information", "Late fee cant be zero");
                newFee.clear();
                return;
            }
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setDouble(1, Double.valueOf(newFee.getText()));
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Save changes ?");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {
                    preparedStatement.executeUpdate();
                    Notification notification = new Notification("Information", "Late fee successfully updated", 3);
                    newFee.clear();
                    fetchCurrentFee();
                }
            } catch (SQLException ex) {
                Logger.getLogger(changeFeePerDayController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(changeFeePerDayController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        newFee.clear();
    }

    private void fetchCurrentFee() {
        String selectQery = "SELECT LateFeePerDay FROM Account";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(selectQery);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double lateFeePerDay = resultSet.getDouble("LateFeePerDay");
                currentFee.setText(String.valueOf(lateFeePerDay));
            }
        } catch (SQLException ex) {
            Logger.getLogger(changeFeePerDayController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(changeFeePerDayController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean validateLateFee() {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(newFee.getText());
        if (matcher.find() && matcher.group().equals(newFee.getText())) {
            return true;
        } else {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.ERROR, "Error", "Late fee is invalid");
            newFee.clear();
            return false;
        }
    }

    private boolean validateField() {
        if (newFee.getText().isEmpty()) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.INFORMATION, "Information", "The field is empty");
            return false;
        }
        return true;
    }
}

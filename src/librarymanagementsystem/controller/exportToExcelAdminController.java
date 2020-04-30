/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class exportToExcelAdminController implements Initializable {

    @FXML
    private VBox vbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        exportToPdfController.animatePane(vbox);
    }

    @FXML
    private void exportToExcel(ActionEvent event) {
        if (exportDataAdminController.type == null) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Report type validation ", "Select report type");
        } else {
            switch (exportDataAdminController.type) {
                case "Librarian": {
                    Task task = new Task() {
                        @Override
                        protected Object call() throws Exception {
                            File reportsFolder = new File(System.getProperty("user.home") + "\\Documents\\Reports");
                            if (!reportsFolder.exists()) {
                                reportsFolder.mkdir();
                            }
                            Connection connection = null;
                            PreparedStatement preparedStatement = null;
                            PreparedStatement preparedStatement1 = null;
                            ResultSet resultSet = null;
                            ResultSet resultSet1 = null;
                            String query = "SELECT * FROM User WHERE Usertype = ?";
                            String countBooksQuery = "SELECT COUNT(*) FROM USER WHERE Usertype = ?";
                            try {
                                connection = DatabaseConnection.Connect();
                                preparedStatement = connection.prepareStatement(query);
                                preparedStatement.setString(1, "Librarian");
                                preparedStatement1 = connection.prepareStatement(countBooksQuery);
                                preparedStatement1.setString(1, "Librarian");
                                resultSet1 = preparedStatement1.executeQuery();
                                resultSet = preparedStatement.executeQuery();
                                int numberOfRows = resultSet1.getInt(1);
                                if (numberOfRows == 0) {
                                    Platform.runLater(() -> {
                                        Notification notification = new Notification("Information", "No librarian records in the system", 3);
                                    });
                                } else {
                                    XSSFWorkbook workbook = new XSSFWorkbook();
                                    XSSFSheet sheet = workbook.createSheet("Librarian");
                                    XSSFRow header = sheet.createRow(0);
                                    header.createCell(0).setCellValue("First Name");
                                    header.createCell(1).setCellValue("Last Name");
                                    header.createCell(2).setCellValue("Username");
                                    header.createCell(3).setCellValue("Email Address");
                                    sheet.autoSizeColumn(0);
                                    sheet.autoSizeColumn(1);
                                    sheet.autoSizeColumn(2);
                                    sheet.setColumnWidth(3, 256 * 25);
                                    sheet.setZoom(150);
                                    int index = 1;
                                    while (resultSet.next()) {
                                        XSSFRow row = sheet.createRow(index);
                                        row.createCell(0).setCellValue(resultSet.getString(2));
                                        row.createCell(1).setCellValue(resultSet.getString(3));
                                        row.createCell(2).setCellValue(resultSet.getString(4));
                                        row.createCell(3).setCellValue(resultSet.getString(5));
                                        index++;
                                    }
                                    File librarianReport = new File(System.getProperty("user.home") + "\\Documents\\Reports\\Librarian.xlsx");
                                    FileOutputStream fileOutputStream = new FileOutputStream(librarianReport);
                                    workbook.write(fileOutputStream);
                                    fileOutputStream.close();
                                    Platform.runLater(() -> {
                                        Notification notifications = new Notification("Information", "Report saved to home/documents/Reports", 3);
                                    });
                                    Thread.sleep(1000);
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Open report");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Open report ?");
                                        Optional<ButtonType> optional = alert.showAndWait();
                                        if (optional.get().equals(ButtonType.OK)) {
                                            Desktop desktop = Desktop.getDesktop();
                                            try {
                                                desktop.open(librarianReport);
                                            } catch (IOException ex) {
                                                Logger.getLogger(exportToExcelAdminController.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    });

                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(exportToExcelAdminController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Platform.runLater(() -> {
                                    Notification notifications = new Notification("Error", "Failed to export librarian details", 3);
                                });
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
                                    Logger.getLogger(exportToExcelAdminController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            return null;
                        }
                    };
                    Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();
                    break;
                }
            }
        }
    }
}

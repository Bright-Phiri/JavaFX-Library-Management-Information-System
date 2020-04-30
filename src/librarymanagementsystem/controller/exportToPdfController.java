/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import java.awt.Desktop;
import java.io.File;
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
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.Notification;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class exportToPdfController implements Initializable {

    @FXML
    private VBox vbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        animatePane(vbox);
    }

    @FXML
    private void exportToPDF(ActionEvent event) {
        if (exportDataController.type == null) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(AlertType.INFORMATION, "Report type validation ", "Select report type");
        } else {
            switch (exportDataController.type) {
                case "Books": {
                    Thread thread = new Thread(new GenerateReport());
                    thread.setDaemon(true);
                    thread.start();
                    break;
                }
                case "Students": {
                    Thread thread = new Thread(new GenerateStudentReport());
                    thread.setDaemon(true);
                    thread.start();
                    break;
                }
            }
        }
    }

    public class SaveReport {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        private void saveReport() {
            File reportsFolder = new File(System.getProperty("user.home") + "\\Documents\\Reports");
            if (!reportsFolder.exists()) {
                reportsFolder.mkdir();
            }
            File file = new File(System.getProperty("user.home") + "\\Documents\\Reports\\Book Information Report.pdf");
            if (file.exists()) {
            } else {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(exportToPdfAdminController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            File file1 = new File(System.getProperty("user.home") + "\\Documents\\Reports\\Book Information Report.pdf");
            String query = "SELECT COUNT(*) FROM Book";
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                int count = resultSet.getInt(1);
                if (count == 0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Notification notification = new Notification("Information", "No book records in the system", 3);
                        }
                    });
                    return;
                }
                if (file.renameTo(file1)) {
                    JasperReport report = JasperCompileManager.compileReport(getClass().getResourceAsStream("/librarymanagementsystem/reports/BookTableReport.jrxml"));
                    JasperPrint print = JasperFillManager.fillReport(report, null, connection);
                    JasperExportManager.exportReportToPdfFile(print, file.toString());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Notification notification = new Notification("Information", "Report saved to Documents/Reports", 3);
                        }
                    });
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Open report");
                        alert.setHeaderText(null);
                        alert.setContentText("Open report ?");
                        Optional<ButtonType> optional = alert.showAndWait();
                        if (optional.get().equals(ButtonType.OK)) {
                            Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.open(file);
                            } catch (IOException ex) {
                                Logger.getLogger(allBooksController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Notification notification = new Notification("Error", "Failed to save the report", 3);
                        }
                    });
                }
            } catch (JRException ex) {
                System.err.println(ex);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Notification notification = new Notification("Error", "Failed to save the report", 3);
                    }
                });
            } catch (SQLException ex) {
                Logger.getLogger(allBooksController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(exportToPdfController.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(allBooksController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private class GenerateReport extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            SaveReport report = new SaveReport();
            report.saveReport();
            return null;
        }
    }

    private class GenerateStudentReport extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            SaveStudentReport report = new SaveStudentReport();
            report.saveReport();
            return null;
        }
    }

    public static void animatePane(VBox box) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), box);
        transition.setFromX(30);
        transition.setToX(-30);
        transition.play();
    }

    public class SaveStudentReport {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        private void saveReport() {
            File reportsFolder = new File(System.getProperty("user.home") + "\\Documents\\Reports");
            if (!reportsFolder.exists()) {
                reportsFolder.mkdir();
            }
            File file = new File(System.getProperty("user.home") + "\\Documents\\Reports\\Student Information Report.pdf");
            if (file.exists()) {
            } else {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(exportToPdfAdminController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            File file1 = new File(System.getProperty("user.home") + "\\Documents\\Reports\\Student Information Report.pdf");
            String query = "SELECT COUNT(*) FROM Student";
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                int count = resultSet.getInt(1);
                if (count == 0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Notification notification = new Notification("Information", "No student records in the system", 3);
                        }
                    });
                    return;
                }
                if (file.renameTo(file1)) {
                    JasperReport report = JasperCompileManager.compileReport(getClass().getResourceAsStream("/librarymanagementsystem/reports/StudentTableReport.jrxml"));
                    JasperPrint print = JasperFillManager.fillReport(report, null, connection);
                    JasperExportManager.exportReportToPdfFile(print, file.toString());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Notification notification = new Notification("Information", "Report saved to Documents/Reports", 3);
                        }
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
                                desktop.open(file);
                            } catch (IOException ex) {
                                Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Notification notification = new Notification("Error", "Failed to save the report", 3);
                        }
                    });
                }
            } catch (JRException ex) {
                System.err.println(ex);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Notification notification = new Notification("Error", "Failed to save the report", 3);
                    }
                });
            } catch (SQLException ex) {
                Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(exportToPdfController.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(studentsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}

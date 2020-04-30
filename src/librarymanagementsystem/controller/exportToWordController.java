/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
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
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class exportToWordController implements Initializable {

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
    private void exportToWord(ActionEvent event) {
        if (exportDataController.type == null) {
            librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Report type validation ", "Select report type");
        } else {
            switch (exportDataController.type) {
                case "Books": {
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
                            String query = "SELECT * FROM Book";
                            String countBooksQuery = "SELECT COUNT(*) FROM Book";
                            try {
                                connection = DatabaseConnection.Connect();
                                preparedStatement = connection.prepareStatement(query);
                                preparedStatement1 = connection.prepareStatement(countBooksQuery);
                                resultSet1 = preparedStatement1.executeQuery();
                                resultSet = preparedStatement.executeQuery();
                                int numberOfRows = resultSet1.getInt(1);
                                if (numberOfRows == 0) {
                                    Platform.runLater(() -> {
                                        Notification notification1 = new Notification("Information", "No books in the system", 3);
                                    });
                                } else {
                                    XWPFDocument document = new XWPFDocument();
                                    XWPFParagraph paragraph = document.createParagraph();
                                    paragraph.setBorderBottom(Borders.APPLES);
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run = paragraph.createRun();
                                    run.setFontSize(24);
                                    run.setText("Library Management System Report");
                                    run.addBreak();
                                    XWPFParagraph paragraph1 = document.createParagraph();
                                    paragraph1.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run1 = paragraph1.createRun();
                                    run1.setFontSize(18);
                                    run1.setText("Books Details");
                                    XWPFTable studentsTable = document.createTable();
                                    studentsTable.setCellMargins(100, 100, 100, 100);
                                    XWPFTableRow row = studentsTable.getRow(0);
                                    row.getCell(0).setText("ISBN");
                                    row.addNewTableCell().setText("Book Title");
                                    row.addNewTableCell().setText("Book Author");
                                    row.addNewTableCell().setText("Publisher");
                                    row.addNewTableCell().setText("Edition");
                                    row.addNewTableCell().setText("Quantity");
                                    row.addNewTableCell().setText("Remaining Books");
                                    while (resultSet.next()) {
                                        row = studentsTable.createRow();
                                        row.getCell(0).setText(resultSet.getString(1));
                                        row.getCell(1).setText(resultSet.getString(2));
                                        row.getCell(2).setText(resultSet.getString(3));
                                        row.getCell(3).setText(resultSet.getString(4));
                                        row.getCell(4).setText("" + resultSet.getInt(5));
                                        row.getCell(5).setText("" + resultSet.getInt(6));
                                        row.getCell(6).setText("" + resultSet.getInt(7));
                                    }
                                    File booksReport = new File(System.getProperty("user.home") + "\\Documents\\Reports\\BooksDetails.docx");
                                    FileOutputStream fileOutputStream = new FileOutputStream(booksReport);
                                    document.write(fileOutputStream);
                                    Platform.runLater(() -> {
                                        Notification notification = new Notification("Information", "Report saved to home/documents/Reports", 3);
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
                                                desktop.open(booksReport);
                                            } catch (IOException ex) {
                                                Logger.getLogger(exportToWordController.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    });
                                }
                            } catch (FileNotFoundException ex) {
                                Platform.runLater(() -> {
                                    Notification notification1 = new Notification("Error", "Failed to export student details", 3);
                                });
                            } catch (SQLException | IOException ex) {
                                Logger.getLogger(exportToWordController.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                try {
                                    if (resultSet != null) {
                                        resultSet.close();
                                    }
                                    if (resultSet1 != null) {
                                        resultSet1.close();
                                    }
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
                                    Logger.getLogger(exportToWordController.class.getName()).log(Level.SEVERE, null, ex);
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
                case "Students": {
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
                            String query = "SELECT * FROM Student";
                            String countBooksQuery = "SELECT COUNT(*) FROM Student";
                            try {
                                connection = DatabaseConnection.Connect();
                                preparedStatement = connection.prepareStatement(query);
                                preparedStatement1 = connection.prepareStatement(countBooksQuery);
                                resultSet1 = preparedStatement1.executeQuery();
                                resultSet = preparedStatement.executeQuery();
                                int numberOfRows = resultSet1.getInt(1);
                                if (numberOfRows == 0) {
                                    Platform.runLater(() -> {
                                        Notification notification = new Notification("Information", "No student records in the system", 3);
                                    });
                                } else {
                                    XWPFDocument document = new XWPFDocument();
                                    XWPFParagraph paragraph = document.createParagraph();
                                    paragraph.setBorderBottom(Borders.APPLES);
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run = paragraph.createRun();
                                    run.setFontSize(24);
                                    run.setText("Library Management System Report");
                                    run.addBreak();
                                    XWPFParagraph paragraph1 = document.createParagraph();
                                    paragraph1.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run1 = paragraph1.createRun();
                                    run1.setFontSize(18);
                                    run1.setText("Students Details");
                                    XWPFTable studentsTable = document.createTable();
                                    studentsTable.setCellMargins(100, 100, 100, 100);
                                    XWPFTableRow row = studentsTable.getRow(0);
                                    row.getCell(0).setText("Student ID");
                                    row.addNewTableCell().setText("Student Name");
                                    row.addNewTableCell().setText("Student Email");
                                    row.addNewTableCell().setText("Student Phone");
                                    while (resultSet.next()) {
                                        row = studentsTable.createRow();
                                        row.getCell(0).setText(resultSet.getString(1));
                                        row.getCell(1).setText(resultSet.getString(2));
                                        row.getCell(2).setText(resultSet.getString(3));
                                        row.getCell(3).setText(resultSet.getString(4));
                                    }
                                    File studentsReport = new File(System.getProperty("user.home") + "\\Documents\\Reports\\StudentsDetails.docx");
                                    FileOutputStream fileOutputStream = new FileOutputStream(studentsReport);
                                    document.write(fileOutputStream);
                                    Platform.runLater(() -> {
                                        Notification notification = new Notification("Information", "Report saved to home/documents/Reports", 3);
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
                                                desktop.open(studentsReport);
                                            } catch (IOException ex) {
                                                Logger.getLogger(exportToWordController.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    });
                                }

                            } catch (SQLException ex) {
                                Logger.getLogger(exportToWordController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (FileNotFoundException ex) {
                                Platform.runLater(() -> {
                                    Notification notification = new Notification("Error", "Failed to export student details", 3);
                                });
                            } finally {
                                try {
                                    if (resultSet != null) {
                                        resultSet.close();
                                    }
                                    if (resultSet1 != null) {
                                        resultSet1.close();
                                    }
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
                                    Logger.getLogger(exportToWordController.class.getName()).log(Level.SEVERE, null, ex);
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

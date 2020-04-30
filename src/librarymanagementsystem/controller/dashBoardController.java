/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.scene.control.gauge.linear.SimpleMetroArcGauge;
import librarymanagementsystem.model.DatabaseConnection;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class dashBoardController implements Initializable {

    @FXML
    private Label minimise;
    @FXML
    private Label fullscreen;
    @FXML
    private Label unfullscreen;
    @FXML
    private Label close;
    @FXML
    private BorderPane graphBoarderpane;
    @FXML
    private SimpleMetroArcGauge allBooksGauge;
    @FXML
    private SimpleMetroArcGauge remainingBooksGauge;
    @FXML
    private SimpleMetroArcGauge issuedBooksGauge;
    @FXML
    private SimpleMetroArcGauge allStudentsGauge;
    @FXML
    private SimpleMetroArcGauge bookHoldersGauge;
    @FXML
    private BarChart<?, ?> barchart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadBookDetailsGraph();
                        loadStudentDetailsGraph();
                        allBooksAndRemainingBooks();
                    }
                });
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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

    private void loadBookDetailsGraph() {
        String selectAllBooks = "SELECT SUM(Quantity) FROM Book";
        String remainingBooks = "SELECT SUM(RemainingBooks) FROM Book";
        String allIssuedBooks = "SELECT COUNT(*) FROM IssueBook";
        String allShortTermIssuedBooks = "SELECT COUNT(*) FROM ShortTermBook";
        Connection connection = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        PreparedStatement ps4 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        try {
            connection = DatabaseConnection.Connect();
            ps1 = connection.prepareStatement(selectAllBooks);
            ps2 = connection.prepareStatement(remainingBooks);
            ps3 = connection.prepareStatement(allIssuedBooks);
            ps4 = connection.prepareStatement(allShortTermIssuedBooks);
            rs1 = ps1.executeQuery();
            rs2 = ps2.executeQuery();
            rs3 = ps3.executeQuery();
            rs4 = ps4.executeQuery();
            int allBooks = rs1.getInt(1);
            int remaining = rs2.getInt(1);
            int issued = rs3.getInt(1);
            int issuedShortTerm = rs4.getInt(1);
            int allIssued  = issued + issuedShortTerm;
            XYChart.Series series1 = new XYChart.Series();
            series1.setName("Book information");
            series1.getData().add(new XYChart.Data<>("All Books", allBooks));
            series1.getData().add(new XYChart.Data<>("Remaining Books", remaining));
            series1.getData().add(new XYChart.Data<>("Issued Books", allIssued));
            barchart.getData().add(series1);
            for (int i = 0; i < series1.getData().size(); i++) {
                XYChart.Data data = (XYChart.Data) series1.getData().get(i);
                Tooltip.install(data.getNode(), new Tooltip(data.getXValue().toString() + " : " + data.getYValue().toString().replace(".0", "")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dashBoardController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs1 != null) {
                    rs1.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (rs3 != null) {
                    rs3.close();
                }
                if (rs4 != null) {
                    rs4.close();
                }
                if (ps1 != null) {
                    ps1.close();
                }
                if (ps2 != null) {
                    ps2.close();
                }
                if (ps3 != null) {
                    ps3.close();
                }
                if (ps4 != null) {
                    ps4.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(dashBoardController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void loadStudentDetailsGraph() {
        String selectAllStudents = "SELECT COUNT(*) FROM Student";
        String allStudentsHoldingBooks = "SELECT StudentID FROM IssueBook";
        String allStudentsHoldingShortTermBooks = "SELECT StudentID FROM ShortTermBook";
        Connection connection = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        try {
            connection = DatabaseConnection.Connect();
            ps1 = connection.prepareStatement(selectAllStudents);
            ps2 = connection.prepareStatement(allStudentsHoldingBooks);
            ps3 = connection.prepareStatement(allStudentsHoldingShortTermBooks);
            rs1 = ps1.executeQuery();
            rs2 = ps2.executeQuery();
            rs3 = ps3.executeQuery();
            Set<String> bookHolders = new HashSet<>();
            while (rs2.next()) {
                bookHolders.add(rs2.getString("StudentID"));
            }
            while (rs3.next()) {
                bookHolders.add(rs3.getString("StudentID"));
            }
            int allStudents = rs1.getInt(1);
            XYChart.Series series2 = new XYChart.Series();
            series2.setName("Student information");
            series2.getData().add(new XYChart.Data<>("All Students", allStudents));
            series2.getData().add(new XYChart.Data<>("Students Holding Books", bookHolders.size()));
            barchart.getData().add(series2);
            for (int i = 0; i < series2.getData().size(); i++) {
                XYChart.Data data = (XYChart.Data) series2.getData().get(i);
                Tooltip.install(data.getNode(), new Tooltip(data.getXValue().toString() + " : " + data.getYValue().toString().replace(".0", "")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dashBoardController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs1 != null) {
                    rs1.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (rs3 != null) {
                    rs3.close();
                }
                if (ps1 != null) {
                    ps1.close();
                }
                if (ps2 != null) {
                    ps2.close();
                }
                if (ps3 != null) {
                    ps3.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(dashBoardController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void allBooksAndRemainingBooks() {
        Connection conn = null;
        PreparedStatement pre1 = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre3 = null;
        PreparedStatement pre4 = null;
        PreparedStatement pre5 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        ResultSet rs5 = null;
        String query1 = "SELECT SUM(Quantity) FROM Book";
        String query2 = "SELECT SUM(RemainingBooks) FROM Book";
        String query3 = "SELECT COUNT(*) FROM Student";
        String query4 = "SELECT StudentID FROM IssueBook";
        String query5 = "SELECT StudentID FROM ShortTermBook";
        try {
            conn = DatabaseConnection.Connect();
            pre1 = conn.prepareStatement(query1);
            rs1 = pre1.executeQuery();
            int allBooks = rs1.getInt(1);
            pre2 = conn.prepareStatement(query2);
            rs2 = pre2.executeQuery();
            int rBooks = rs2.getInt(1);
            pre3 = conn.prepareStatement(query3);
            rs3 = pre3.executeQuery();
            int allStudents = rs3.getInt(1);
            pre4 = conn.prepareStatement(query4);
            rs4 = pre4.executeQuery();
            pre5 = conn.prepareStatement(query5);
            rs5 = pre5.executeQuery();
            Set<String> set = new HashSet<>();
            while (rs4.next()) {
                set.add(rs4.getString("StudentID"));
            }
            while (rs5.next()) {
                set.add(rs5.getString("StudentID"));
            }
            if (allStudents == 0) {
            } else {
                allStudentsGauge.setMaxValue(allStudents);
                allStudentsGauge.setValue(allStudents);
                bookHoldersGauge.setMaxValue(allStudents);
                bookHoldersGauge.setValue(set.size());
            }
            int issuedBooks = allBooks - rBooks;
            if (allBooks == 0) {
            } else {
                allBooksGauge.setMaxValue(allBooks);
                allBooksGauge.setValue(allBooks);
                remainingBooksGauge.setMaxValue(allBooks);
                remainingBooksGauge.setValue(rBooks);
                issuedBooksGauge.setMaxValue(allBooks);
                issuedBooksGauge.setValue(issuedBooks);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (rs1 != null) {
                    rs1.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (rs3 != null) {
                    rs3.close();
                }
                if (rs4 != null) {
                    rs4.close();
                }
                if (rs5 != null) {
                    rs5.close();
                }
                if (pre1 != null) {
                    pre1.close();
                }
                if (pre2 != null) {
                    pre2.close();
                }
                if (pre3 != null) {
                    pre3.close();
                }
                if (pre4 != null) {
                    pre4.close();
                }
                if (pre5 != null) {
                    pre5.close();
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

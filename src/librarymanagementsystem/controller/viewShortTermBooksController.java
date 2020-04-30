/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.IssuedBook;
import librarymanagementsystem.model.MailServer;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class viewShortTermBooksController implements Initializable {

    @FXML
    private TableColumn<IssuedBook, String> idCol;
    @FXML
    private TableColumn<IssuedBook, String> isbnCol;
    @FXML
    private TableColumn<IssuedBook, String> titleCol;
    @FXML
    private TableColumn<IssuedBook, String> studentIdCol;
    @FXML
    private TableColumn<IssuedBook, String> nameCol;
    @FXML
    private TableColumn<IssuedBook, String> issuedTimeCol;
    @FXML
    private TableColumn<IssuedBook, String> returnTimeCol;
    @FXML
    private TableColumn<IssuedBook, Integer> hoursCol;
    @FXML
    private TableColumn<IssuedBook, Double> feeCol;
    @FXML
    private TableView<IssuedBook> issuedTable;
    ObservableList<IssuedBook> data = FXCollections.observableArrayList();
    static int counter = 0;
    public static TableView tableView;
    public static boolean isThread1Running = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableView = issuedTable;
        counter = 0;
        initializeColumns();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                checkLateFee();
                if (!viewIssuedBooksController.isThread1Running && !viewIssuedBooksController.isThread2Running) {
                    isThread1Running = true;
                    updateFee();
                }
                return null;
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                loadData();
                isThread1Running = false;
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void initializeColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("issuedID"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        issuedTimeCol.setCellValueFactory(new PropertyValueFactory<>("issuedTime"));
        returnTimeCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("days"));
        feeCol.setCellValueFactory(new PropertyValueFactory<>("fee"));
    }

    public void loadData() {
        data.clear();
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM ShortTermBook";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                data.add(new IssuedBook(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getInt(8), rs.getDouble(9)));
            }
            issuedTable.setItems(data);
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                if (pre != null) {
                    pre.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public static void notifyShortTermStudents() {
        int numberOsSentEmails = 0;
        String selectDate = "SELECT IssuedID,ReturnTime FROM ShortTermBook";
        String updateQuery = "UPDATE ShortTermBook SET Notify = ? WHERE IssuedID = ?";
        String selectEmail = "SELECT * FROM Student WHERE StudentID = ?";
        String selectToNotify = "SELECT * FROM ShortTermBook WHERE IssuedId = ?";
        String updateInitialLateFee = "UPDATE ShortTermBook SET Fee = ? WHERE IssuedID = ?";
        String selectLateFee = "SELECT LateFeePerHour FROM Account";
        Connection connection = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        PreparedStatement preparedStatement3 = null;
        PreparedStatement preparedStatement4 = null;
        PreparedStatement preparedStatement5 = null;
        PreparedStatement preparedStatement6 = null;
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;
        ResultSet resultSet3 = null;
        ResultSet resultSet4 = null;
        LocalTime time = LocalTime.now();
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement1 = connection.prepareStatement(selectDate);
            preparedStatement2 = connection.prepareStatement(updateQuery);
            preparedStatement3 = connection.prepareStatement(selectEmail);
            preparedStatement4 = connection.prepareStatement(selectToNotify);
            preparedStatement5 = connection.prepareStatement(updateInitialLateFee);
            preparedStatement6 = connection.prepareStatement(selectLateFee);
            resultSet1 = preparedStatement1.executeQuery();
            resultSet4 = preparedStatement6.executeQuery();
            double lateFee = 0;
            if (resultSet4.next()) {
                lateFee = resultSet4.getDouble("LateFeePerHour");
            }
            while (resultSet1.next()) {
                int issuedId = resultSet1.getInt(1);
                LocalTime localDate = LocalTime.parse(resultSet1.getString("ReturnTime"));
                if (time.isAfter(localDate)) {
                    preparedStatement4.setInt(1, issuedId);
                    resultSet2 = preparedStatement4.executeQuery();
                    if (resultSet2.getString("Notify").equals("False")) {
                        System.out.println("notifying ");
                        preparedStatement3.setString(1, resultSet2.getString("StudentID"));
                        resultSet3 = preparedStatement3.executeQuery();
                        Format format = new SimpleDateFormat("MM/dd/yyyy");
                        BufferedImage bufferedImage = new BufferedImage(300, 350, BufferedImage.TYPE_INT_RGB);
                        Graphics2D graphics2D = bufferedImage.createGraphics();
                        graphics2D.setBackground(Color.decode("#4CAF50"));
                        graphics2D.clearRect(0, 0, 300, 350);
                        Graphics graphics = bufferedImage.getGraphics();
                        graphics.setFont(graphics.getFont().deriveFont(15f));
                        graphics.drawString("Report Date", 20, 20);
                        graphics.drawString(format.format(new Date()), 20, 40);
                        graphics.drawString("Book Name", 20, 65);
                        graphics.drawString(resultSet2.getString("BookName"), 20, 80);
                        graphics.drawString("Issued Time", 20, 105);
                        graphics.drawString(resultSet2.getString("IssuedTime"), 20, 120);
                        graphics.drawString("Due Time", 20, 144);
                        graphics.drawString(resultSet2.getString("ReturnTime"), 20, 160);
                        graphics.drawString("Hours used after Due Time", 20, 185);
                        graphics.drawString(String.valueOf(ChronoUnit.HOURS.between(LocalTime.parse(resultSet2.getString("ReturnTime")), time)), 20, 205);
                        graphics.drawString("Fine per hour (MK)", 20, 235);
                        graphics.drawString(String.valueOf(lateFee), 20, 250);
                        graphics.drawString("Total Amount (MK)", 20, 275);
                        double totalFine = ChronoUnit.HOURS.between(LocalTime.parse(resultSet2.getString("ReturnTime")), time) * lateFee;
                        graphics.drawString(String.valueOf(totalFine), 20, 290);
                        File imageFile = new File("image.png");
                        if (imageFile.exists()) {
                            imageFile.delete();
                        } else {
                            imageFile.createNewFile();
                        }
                        ImageIO.write(bufferedImage, "png", imageFile);
                        if (MailServer.getMailServerInformation() == null) {
                            Platform.runLater(() -> {
                                Notification notification = new Notification("Information", "Mail server not configured", 3);
                            });
                        } else {
                            MailServer mailServer = MailServer.getMailServerInformation();
                            Properties pro = System.getProperties();
                            pro.put("mail.smtp.host", mailServer.getServerName());
                            pro.put("mail.smtp.port", mailServer.getPort());
                            pro.put("mail.smtp.auth", mailServer.getAuthentication());
                            pro.put("mail.smtp.starttls.enable", mailServer.getEnableTls());

                            Session session = Session.getDefaultInstance(pro, new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(mailServer.getSystemEmail(), mailServer.getEmailPassword());
                                }
                            });
                            Transport transport = session.getTransport("smtp");
                            transport.connect(mailServer.getServerName(), mailServer.getSystemEmail(), mailServer.getEmailPassword());
                            Message message = new MimeMessage(session);
                            message.setSubject("Library Overdue Notification");
                            message.setFrom(new InternetAddress(mailServer.getSystemEmail(), "Library"));
                            message.addRecipient(Message.RecipientType.TO, new InternetAddress(resultSet3.getString("Email")));
                            BodyPart bodyPart = new MimeBodyPart();
                            bodyPart.setContent("<h2>Library Overdue Reminder<h2>", "text/html");
                            Multipart multipart = new MimeMultipart();
                            multipart.addBodyPart(bodyPart);
                            bodyPart = new MimeBodyPart();
                            bodyPart.setFileName(imageFile.getAbsolutePath());
                            DataSource dataSource = new FileDataSource(imageFile.getAbsolutePath());
                            bodyPart.setDataHandler(new DataHandler(dataSource));
                            multipart.addBodyPart(bodyPart);
                            message.setContent(multipart);
                            transport.sendMessage(message, message.getAllRecipients());
                            preparedStatement2.setString(1, "True");
                            preparedStatement2.setInt(2, issuedId);
                            preparedStatement2.executeUpdate();
                            counter++;
                            numberOsSentEmails++;
                        }
                    }
                } else {
                    preparedStatement2.setString(1, "False");
                    preparedStatement2.setInt(2, issuedId);
                    preparedStatement5.setInt(1, issuedId);
                    preparedStatement5.executeUpdate();
                    preparedStatement2.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(viewShortTermBooksController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Platform.runLater(() -> {
                Notification notification = new Notification("Information", "Failed to notify student(s), Please check your internet connection", 3);
            });
        } catch (IOException ex) {
            Logger.getLogger(viewShortTermBooksController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet1 != null) {
                    resultSet1.close();
                }
                if (resultSet2 != null) {
                    resultSet2.close();
                }
                if (resultSet3 != null) {
                    resultSet3.close();
                }
                if (resultSet4 != null) {
                    resultSet4.close();
                }
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
                if (preparedStatement3 != null) {
                    preparedStatement3.close();
                }
                if (preparedStatement4 != null) {
                    preparedStatement4.close();
                }
                if (preparedStatement5 != null) {
                    preparedStatement5.close();
                }
                if (preparedStatement6 != null) {
                    preparedStatement6.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(viewShortTermBooksController.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (counter == 1) {
                Platform.runLater(() -> {
                    Notification notification = new Notification("Information", "Book Overdue Short Term Loan Notification sent to  " + counter + " student", 3);
                });
            }
            if (counter > 1) {
                Platform.runLater(() -> {
                    Notification notification = new Notification("Information", "Book Overdue Short Term Loan Notification sent to  " + counter + " students", 3);
                });
            }
        }
    }

    public void updateFee() {
        String selectDate = "SELECT IssuedID,ReturnTime FROM ShortTermBook";
        String updateQuery = "UPDATE ShortTermBook SET Fee = ? WHERE IssuedID = ?";
        String selectLateFee = "SELECT LateFeePerHour FROM Account";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSe2 = null;
        ResultSet resultSet = null;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);
        String formattedDateTime = formatter.format(now);
        LocalDateTime time1 = LocalDateTime.parse(formattedDateTime, formatter);
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(selectDate);
            preparedStatement1 = connection.prepareStatement(updateQuery);
            preparedStatement2 = connection.prepareStatement(selectLateFee);
            resultSe2 = preparedStatement2.executeQuery();
            double lateFee = resultSe2.getDouble("LateFeePerHour");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int issuedId = resultSet.getInt(1);
                LocalDateTime time = LocalDateTime.parse(resultSet.getString("ReturnTime"),formatter);
                if (time1.isAfter(time)) {
                    int isHourReached = (int) ChronoUnit.HOURS.between(time, now);
                    if (isHourReached == 0) {
                        preparedStatement1.setDouble(1, 1 * lateFee);
                        preparedStatement1.setInt(2, issuedId);
                        preparedStatement1.executeUpdate();
                    } else {
                        int hours = (int) ChronoUnit.HOURS.between(time, now);
                        double tFee = ChronoUnit.HOURS.between(now, time) * lateFee;
                        if (tFee < 0) {
                            tFee = tFee * -1;
                        }
                        preparedStatement1.setDouble(1, tFee);
                        preparedStatement1.setInt(2, issuedId);
                        preparedStatement1.executeUpdate();
                    }

                } else {
                    double initialFee = 0.00;
                    preparedStatement1.setDouble(1, initialFee);
                    preparedStatement1.setInt(2, issuedId);
                    preparedStatement1.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(viewShortTermBooksController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSe2 != null) {
                    resultSe2.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(viewShortTermBooksController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean checkLateFee() {
        String query = "SELECT * FROM Account";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double lateFeePerHour = resultSet.getDouble(3);
                if (lateFeePerHour == 0.0) {
                    Notification notification = new Notification("Information", "Contact Administrator to set late fee in settings panel", 5);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(viewShortTermBooksController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(viewShortTermBooksController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    public ObservableList getData() {
        ObservableList<IssuedBook> information = FXCollections.observableArrayList();
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM ShortTermBook";
        information.clear();
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                information.add(new IssuedBook(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getInt(8), rs.getDouble(9)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(viewShortTermBooksController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (pre != null) {
                    pre.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
        return information;
    }
}

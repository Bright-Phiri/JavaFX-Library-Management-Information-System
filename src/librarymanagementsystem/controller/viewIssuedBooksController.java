package librarymanagementsystem.controller;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import librarymanagementsystem.model.IssuedBook;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
import librarymanagementsystem.model.MailServer;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class viewIssuedBooksController implements Initializable {

    @FXML
    private BorderPane boarderpane;
    @FXML
    private Label minimise;
    @FXML
    private Label fullscreen;
    @FXML
    private Label unfullscreen;
    @FXML
    private Label close;
    @FXML
    private Label word;
    @FXML
    private TableView<IssuedBook> tableView;
    @FXML
    private TableColumn<IssuedBook, String> issuedID;
    @FXML
    private TableColumn<IssuedBook, String> bookID;
    @FXML
    private TableColumn<IssuedBook, String> studentID;
    @FXML
    private TableColumn<IssuedBook, String> issuedDateAndTime;
    @FXML
    private TableColumn<IssuedBook, String> dueDate;
    @FXML
    private TableColumn<IssuedBook, String> bookName;
    @FXML
    private TableColumn<IssuedBook, String> studentName;
    @FXML
    private TableColumn<IssuedBook, Double> fee;
    @FXML
    private TableColumn<IssuedBook, Integer> days;
    ObservableList<IssuedBook> data = FXCollections.observableArrayList();
    @FXML
    private TextField searchField;
    @FXML
    private ImageView spinner;
    @FXML
    private ProgressBar prograssBar;
    static int counter = 0;
    @FXML
    private ComboBox<String> issueTypeCombobox;
    public static boolean isThread1Running = false;
    public static boolean isThread2Running = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        issueTypeCombobox.getItems().setAll(FXCollections.observableArrayList("Long Term", "Short Term"));
        issueTypeCombobox.setValue("Long Term");
        counter = 0;
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
        initializeColumns();
        checkLateFee();
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                int max = 40;
                for (int i = 0; i <= max; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(i, max);
                    Thread.sleep(25);
                }
                return max;
            }
        };

        prograssBar.progressProperty().bind(task.progressProperty());

        task.setOnRunning(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                updateFee();
            }
        });
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                spinner.setVisible(false);
                loadData();
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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

    private void initializeColumns() {
        issuedID.setCellValueFactory(new PropertyValueFactory<>("issuedID"));
        bookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        studentID.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        issuedDateAndTime.setCellValueFactory(new PropertyValueFactory<>("issuedTime"));
        dueDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        bookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        studentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        days.setCellValueFactory(new PropertyValueFactory<>("days"));
        fee.setCellValueFactory(new PropertyValueFactory<>("fee"));
    }

    public void loadData() {
        data.clear();
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM IssueBook";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                data.add(new IssuedBook(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getInt(8), rs.getDouble(9)));
            }
            tableView.getItems().setAll(data);
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
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                Thread.sleep(3000);
                notifyStudents();
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void notifyStudents() {
        String selectDate = "SELECT IssuedID,ReturnDate FROM IssueBook";
        String updateQuery = "UPDATE IssueBook SET Notify = ? WHERE IssuedID = ?";
        String selectEmail = "SELECT * FROM Student WHERE StudentID = ?";
        String selectToNotify = "SELECT * FROM IssueBook WHERE IssuedId = ?";
        String updateInitialLateFee = "UPDATE IssueBook SET Fee = ? WHERE IssuedID = ?";
        String selectLateFee = "SELECT LateFeePerDay FROM Account";
        Connection connection = null;
        boolean isMailServerConfigured = false;
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
        LocalDate today = LocalDate.now();
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
                lateFee = resultSet4.getDouble("LateFeePerDay");
            }
            while (resultSet1.next()) {
                int issuedId = resultSet1.getInt(1);
                LocalDate localDate = LocalDate.parse(resultSet1.getString("ReturnDate"));
                if (today.isAfter(localDate)) {
                    preparedStatement4.setInt(1, issuedId);
                    resultSet2 = preparedStatement4.executeQuery();
                    if (resultSet2.getString("Notify").equals("False")) {
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
                        graphics.drawString("Issued Date", 20, 105);
                        graphics.drawString(resultSet2.getString("IssuedDate"), 20, 120);
                        graphics.drawString("Due Date", 20, 144);
                        graphics.drawString(resultSet2.getString("ReturnDate"), 20, 160);
                        graphics.drawString("Days used after Due Date", 20, 185);
                        graphics.drawString(String.valueOf(ChronoUnit.DAYS.between(LocalDate.parse(resultSet2.getString("ReturnDate")), today)), 20, 205);
                        graphics.drawString("Fine per day (MK)", 20, 235);
                        graphics.drawString(String.valueOf(lateFee), 20, 250);
                        graphics.drawString("Total Amount (MK)", 20, 275);
                        double totalFine = ChronoUnit.DAYS.between(LocalDate.parse(resultSet2.getString("ReturnDate")), today) * lateFee;
                        graphics.drawString(String.valueOf(totalFine), 20, 290);
                        File imageFile = new File("image.png");
                        if (imageFile.exists()) {
                            imageFile.delete();
                        } else {
                            imageFile.createNewFile();
                        }
                        ImageIO.write(bufferedImage, "png", imageFile);
                        if (MailServer.getMailServerInformation() == null) {
                            isMailServerConfigured = false;
                        } else {
                            isMailServerConfigured = true;
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
            if (!MailServer.isMailServerConfigured()) {
                Platform.runLater(() -> {
                    Notification notification = new Notification("Information", "Mail server not configured", 3);
                });
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(viewIssuedBooksController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Platform.runLater(() -> {
                Notification notification = new Notification("Information", "Failed to notify students, Please check your internet connection", 3);
            });
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
                Logger.getLogger(viewIssuedBooksController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Platform.runLater(() -> {
                if (counter == 1) {
                    Platform.runLater(() -> {
                        Notification notification = new Notification("Information", "Book Overdue Notification sent to  " + counter + " student", 3);
                    });
                }
                if (counter > 1) {
                    Platform.runLater(() -> {
                        Notification notification = new Notification("Information", "Book Overdue Notification sent to  " + counter + " students", 3);
                    });
                }
            });
        }
    }

    public void updateFee() {
        String selectDate = "SELECT IssuedID,ReturnDate FROM IssueBook";
        String updateQuery = "UPDATE IssueBook SET Fee = ? WHERE IssuedID = ?";
        String selectLateFee = "SELECT LateFeePerDay FROM Account";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSe2 = null;
        ResultSet resultSet = null;
        LocalDate today = LocalDate.now();
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(selectDate);
            preparedStatement1 = connection.prepareStatement(updateQuery);
            preparedStatement2 = connection.prepareStatement(selectLateFee);
            resultSe2 = preparedStatement2.executeQuery();
            double lateFee = resultSe2.getDouble("LateFeePerDay");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int issuedId = resultSet.getInt(1);
                LocalDate localDate = LocalDate.parse(resultSet.getString("ReturnDate"));
                if (today.isAfter(localDate)) {
                    double tFee = ChronoUnit.DAYS.between(localDate, today) * lateFee;
                    if (tFee < 0) {
                        tFee = tFee * -1;
                    }
                    preparedStatement1.setDouble(1, tFee);
                    preparedStatement1.setInt(2, issuedId);
                    preparedStatement1.executeUpdate();
                } else {
                    double initialFee = 0.00;
                    preparedStatement1.setDouble(1, initialFee);
                    preparedStatement1.setInt(2, issuedId);
                    preparedStatement1.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(viewIssuedBooksController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(viewIssuedBooksController.class.getName()).log(Level.SEVERE, null, ex);
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
                double lateFeePerDay = resultSet.getDouble(2);
                if (lateFeePerDay == 0.0) {
                    Notification notification = new Notification("Information", "Contact Administrator to set late fee in settings panel", 5);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(viewIssuedBooksController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    @FXML
    private void searchBook(KeyEvent event) {
        viewShortTermBooksController controller = new viewShortTermBooksController();
        if (issueTypeCombobox.getValue().equals("Long Term")) {
            handleFilteredSeatch(data, tableView);
        }
        if (issueTypeCombobox.getValue().equals("Short Term")) {
            handleFilteredSeatch(controller.getData(), viewShortTermBooksController.tableView);
        }
    }

    private void handleFilteredSeatch(ObservableList<IssuedBook> list, TableView tableView) {
        FilteredList<IssuedBook> filteredListl = new FilteredList<>(list, p -> true);
        searchField.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredListl.setPredicate((Predicate<? super IssuedBook>) book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filterToLowerCase = newValue.toLowerCase();
                if (book.getBookID().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (book.getBookName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (book.getStudentID().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (book.getStudentName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (String.valueOf(book.getIssuedID()).contains(filterToLowerCase)) {
                    return true;
                }
                if (book.getReturnDate().contains(filterToLowerCase)) {
                    return true;
                }
                if (book.getIssuedTime().contains(filterToLowerCase)) {
                    return true;
                }
                if (String.valueOf(book.getDays()).contains(filterToLowerCase)) {
                    return true;
                }
                tableView.setPlaceholder(new Text("No record match your search"));
                return false;
            });
            SortedList<IssuedBook> sortedList = new SortedList<>(filteredListl);
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.getItems().setAll(sortedList);
        }));
    }

    @FXML
    private void selectIssueType(ActionEvent event) throws IOException {
        if (issueTypeCombobox.getValue().equals("Short Term")) {
            spinner.setVisible(true);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/librarymanagementsystem/view/viewShortTermBooks.fxml"));
            BorderPane borderPane = loader.load();
            Service<Integer> service = new Service<Integer>() {
                @Override
                protected Task createTask() {
                    return new Task() {
                        @Override
                        protected Object call() throws Exception {
                            int max = 40;
                            for (int i = 0; i <= max; i++) {
                                if (isCancelled()) {
                                    break;
                                }
                                updateProgress(i, max);
                                Thread.sleep(25);
                            }
                            return max;
                        }
                    };
                }
            };
            prograssBar.progressProperty().bind(service.progressProperty());
            service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    spinner.setVisible(false);
                    boarderpane.setCenter(borderPane);
                }
            });
            Platform.runLater(() -> {
                service.start();
            });
        }
        if (issueTypeCombobox.getValue().equals("Long Term")) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/librarymanagementsystem/view/viewIssuedBooks.fxml"));
            BorderPane borderPane = loader.load();
            mainController.pane.setCenter(borderPane);
        }
    }

}

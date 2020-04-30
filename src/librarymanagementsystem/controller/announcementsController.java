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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.MailServer;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class announcementsController implements Initializable {

    @FXML
    private Label minimise;
    @FXML
    private Label fullscreen;
    @FXML
    private Label unfullscreen;
    @FXML
    private Label close;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField subject;
    @FXML
    private TextArea message;
    @FXML
    private Label status;
    @FXML
    private ProgressBar progress;
    int counter = 0;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        subject.setUserData("subject");
        message.setUserData("message");
        requestFocus(subject);
        requestFocus(message);

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

    @FXML
    private void cancel(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void sendAnnouncement(ActionEvent event) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                List<String> adressses = new ArrayList<>();
                Connection connection = null;
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                PreparedStatement preparedStatement1 = null;
                ResultSet resultSet1 = null;
                String query = "SELECT * FROM Student";
                String count = "SELECT COUNT(*) FROM Student";
                if (MailServer.getMailServerInformation() == null) {
                    Platform.runLater(() -> {
                        Notification notification = new Notification("Information", "Mail server not configured", 3);
                    });
                } else {
                    if (validateFields()) {
                        MailServer mailServer = MailServer.getMailServerInformation();
                        try {
                            connection = DatabaseConnection.Connect();
                            preparedStatement = connection.prepareStatement(query);
                            preparedStatement1 = connection.prepareStatement(count);
                            resultSet1 = preparedStatement1.executeQuery();
                            int numberOfStudents = resultSet1.getInt(1);
                            if (numberOfStudents == 0) {
                                Platform.runLater(() -> {
                                    Notification notification = new Notification("Information", "No student records in the system", 3);
                                });
                            } else {
                                resultSet = preparedStatement.executeQuery();
                                while (resultSet.next()) {
                                    adressses.add(resultSet.getString(3));
                                }
                            }
                            Properties pro = new Properties();
                            pro.put("mail.smtp.host", mailServer.getServerName());
                            pro.put("mail.smtp.port", mailServer.getPort());
                            pro.put("mail.smtp.auth", mailServer.getAuthentication());
                            pro.put("mail.smtp.starttls.enable", mailServer.getEnableTls());

                            Session session = Session.getInstance(pro, new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(mailServer.getSystemEmail(), mailServer.getEmailPassword());
                                }
                            });
                            Transport transport = session.getTransport("smtp");
                            MimeMessage mail = new MimeMessage(session);
                            mail.setSubject(subject.getText());
                            mail.setFrom(new InternetAddress(mailServer.getSystemEmail(), "Librarian"));
                            mail.setSentDate(new Date());
                            mail.setText(message.getText());
                            transport.connect(mailServer.getServerName(), mailServer.getSystemEmail(), mailServer.getEmailPassword());
                            for (int i = 0; i < adressses.size(); i++) {
                                if (isCancelled()) {
                                    break;
                                }
                                mail.addRecipient(Message.RecipientType.TO, new InternetAddress(adressses.get(i)));
                                mail.saveChanges();
                                transport.sendMessage(mail, mail.getAllRecipients());
                                counter++;
                                updateMessage("Sending");
                                updateProgress(i, adressses.size());
                            }
                        } catch (AddressException ex) {
                            Logger.getLogger(announcementsController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (MessagingException ex) {
                            Platform.runLater(() -> {
                                Notification notification = new Notification("Information", "Check your internet connection", 3);
                            });
                            counter = 0;
                        } catch (SQLException ex) {
                            Logger.getLogger(announcementsController.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                if (resultSet != null) {
                                    resultSet.close();
                                }
                                if (preparedStatement != null) {
                                    preparedStatement.close();
                                }
                                if (resultSet1 != null) {
                                    resultSet1.close();
                                }
                                if (preparedStatement1 != null) {
                                    preparedStatement1.close();
                                }
                                if (connection != null) {
                                    connection.close();
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(announcementsController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        Platform.runLater(() -> {
                            if (counter == 1) {
                                Notification notification = new Notification("Information", "Announcement sent to " + counter + " student", 3);
                            }
                            if (counter > 1) {
                                Notification notification = new Notification("Information", "Announcement sent to " + counter + " students", 3);
                            }
                        });
                    }
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        status.visibleProperty().bind(task.runningProperty());
        status.textProperty().bind(task.messageProperty());
        progress.visibleProperty().bind(task.runningProperty());
        progress.progressProperty().bind(task.progressProperty());
    }

    private void requestFocus(TextInputControl field) {
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    if (field.getUserData().equals("subject")) {
                        message.requestFocus();
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (field.getUserData().equals("message")) {
                        subject.requestFocus();
                    }
                }
            }
        });
    }

    private boolean validateFields() {
        if (subject.getText().isEmpty() || message.getText().isEmpty()) {
            Platform.runLater(() -> {
                librarymanagementsystem.model.Alert alert = new librarymanagementsystem.model.Alert(Alert.AlertType.INFORMATION, "Field validation", "Please enter in all fields");
            });
            return false;
        }
        return true;
    }

    private void clearFields() {
        subject.clear();
        message.clear();
    }
}

package librarymanagementsystem.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import librarymanagementsystem.model.Alert;
import librarymanagementsystem.model.DatabaseConnection;
import librarymanagementsystem.model.LoadStage;
import librarymanagementsystem.model.MailServer;
import librarymanagementsystem.model.Notification;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class forgetController implements Initializable {

    @FXML
    private Label close;
    @FXML
    private Label minimize;
    @FXML
    private TextField emailTextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void close(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private boolean validateEmail() {
        Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(_A-Za-z0-9-)*@[A-Za-z0-9-]+(\\.[A-Za-z]+)*(\\.[A-Za-z]{2,})$");
        Matcher M = p.matcher(emailTextField.getText());
        if (M.find() && M.group().equals(emailTextField.getText())) {
            return true;
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR, "Email validation", "Email is invalid");
            });
            return false;
        }
    }

    private boolean validateEmailField() {
        if (emailTextField.getText().isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.INFORMATION, "Field validation", "The field is empty");
            });
            return false;
        }
        return true;
    }

    @FXML
    private void sendEmail(ActionEvent event) {
        Thread thread = new Thread(new SendEmail());
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void Back(ActionEvent event) throws IOException {
        LoadStage stage = new LoadStage("/librarymanagementsystem/view/login.fxml", emailTextField);
    }

    private class SendEmail extends Task<Void> {
        Connection conn = null;
        PreparedStatement pre1 = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre3;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        String query1 = "SELECT * FROM User WHERE Email = ?";
        String query2 = "SELECT Password FROM User WHERE Email  = ?";
        String selectQuery = "SELECT COUNT(*) FROM User";

        @Override
        protected Void call() throws Exception {
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
                if (validateEmailField() && validateEmail()) {
                    try {
                        conn = DatabaseConnection.Connect();
                        pre1 = conn.prepareStatement(query1);
                        pre2 = conn.prepareStatement(query2);
                        pre3 = conn.prepareStatement(selectQuery);
                        rs2 = pre3.executeQuery();
                        int numberOfRows = rs2.getInt(1);
                        if (numberOfRows == 0) {
                            Platform.runLater(() -> {
                                Alert alert = new Alert(AlertType.INFORMATION, "Information", "There is no user account, create a new account");
                            });
                        } else {
                            pre1.setString(1, emailTextField.getText());
                            rs = pre1.executeQuery();
                            if (rs.next()) {
                                pre2.setString(1, emailTextField.getText());
                                rs1 = pre2.executeQuery();
                                String userPassword = rs.getString("Password");
                                MimeMessage message = new MimeMessage(session);
                                boolean isSent = mailServer.sendEmail(session, "System Password", "Library", emailTextField.getText(), "The system password is " + "''" + userPassword + "''");
                                if (isSent) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            Notification notification = new Notification("Information", "Password sent to your email", 5);
                                            emailTextField.clear();
                                        }
                                    });
                                }
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Alert alert = new Alert(AlertType.ERROR, "Error", "Email is not correct");
                                    }
                                });
                            }
                        }

                    } catch (SQLException ex) {
                        System.err.println(ex);
                    } finally {
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                            if (rs1 != null) {
                                rs1.close();
                            }
                            if (rs2 != null) {
                                rs2.close();
                            }
                            if (pre3 != null) {
                                pre3.close();
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
            return null;
        }
    }
}

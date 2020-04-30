/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.model;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Bright
 */
public class MailServer {

    String serverName;
    int port;
    String systemEmail;
    String emailPassword;
    String authentication;
    String enableTls;

    public MailServer() {
    }

    public MailServer(String serverName, int port, String systemEmail, String emailPassword, String authentication, String enableTls) {
        this.serverName = serverName;
        this.port = port;
        this.systemEmail = systemEmail;
        this.emailPassword = emailPassword;
        this.authentication = authentication;
        this.enableTls = enableTls;
    }

    public String getServerName() {
        return serverName;
    }

    public int getPort() {
        return port;
    }

    public String getSystemEmail() {
        return systemEmail;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public String getAuthentication() {
        return authentication;
    }

    public String getEnableTls() {
        return enableTls;
    }

    public static MailServer getMailServerInformation() {
        MailServer mailServer;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM MailServer";
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                mailServer = new MailServer(resultSet.getString(1), resultSet.getInt(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6));
                return mailServer;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MailServer.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(MailServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mailServer = null;
    }

    public static boolean isMailServerConfigured() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM MailServer";
        try {
            connection = DatabaseConnection.Connect();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MailServer.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(MailServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public boolean sendEmail(Session session, String subject, String outgoingName, String recipient, String message) {
        MailServer mailServer = getMailServerInformation();
        try {
            Message mail = new MimeMessage(session);
            mail.setSubject(subject);
            mail.setFrom(new InternetAddress(mailServer.getSystemEmail(), outgoingName));
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            mail.setText(message);
            mail.setSentDate(new Date());
            mail.saveChanges();
            Transport transport = session.getTransport("smtp");
            transport.connect(mailServer.getServerName(), mailServer.getSystemEmail(), mailServer.getEmailPassword());
            transport.sendMessage(mail, mail.getAllRecipients());
            return true;
        } catch (MessagingException ex) {
            Platform.runLater(() -> {
                Notification notification = new Notification("Information", "Make sure there is internet connection", 3);

            });
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MailServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}

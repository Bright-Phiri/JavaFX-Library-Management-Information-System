package librarymanagementsystem.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import librarymanagementsystem.model.DatabaseConnection;

/**
 * FXML Controller class
 *
 * @author Bright
 */
public class loadController implements Initializable {

    @FXML
    private Label text;
    @FXML
    private Label developer;
    @FXML
    private Label close;
    @FXML
    private Label minimize;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DatabaseConnection.checkTable("User", "CREATE TABLE `library`.`user` ( `ID` INT NOT NULL AUTO_INCREMENT , `FirstName` TEXT NULL , `LastName` TEXT NULL , `Username` TEXT NOT NULL , `Email` TEXT NOT NULL , `Password` TEXT NULL , `Usertype` TEXT NOT NULL , PRIMARY KEY (`ID`)) ENGINE = InnoDB; ");
        DatabaseConnection.checkTable("Fee", "CREATE TABLE `library`.`fee` ( `BookID` TEXT NOT NULL , `BookName` TEXT NOT NULL , `StudentID` TEXT NOT NULL , `StudentName` TEXT NOT NULL , `BorrowedDate` TEXT NOT NULL , `ReturnDate` TEXT NOT NULL , `LateFee` DOUBLE NOT NULL , `Username` TEXT NOT NULL , `Date` TEXT NOT NULL ) ENGINE = InnoDB; ");
        DatabaseConnection.checkTable("Book", "CREATE TABLE `library`.`book` ( `BookID` TEXT NOT NULL , `Name` TEXT NOT NULL , `Author` TEXT NOT NULL , `Publisher` TEXT NOT NULL , `Edition` INT NOT NULL , `Quantity` INT NOT NULL , `RemainingBooks` INT NULL DEFAULT '0' , `Availability` TEXT NULL DEFAULT 'Available' , `Section` TEXT NOT NULL) ENGINE = InnoDB;");
        DatabaseConnection.checkTable("Student", "CREATE TABLE `library`.`student` ( `StudentID` TEXT NOT NULL , `Name` TEXT NOT NULL , `Email` TEXT NOT NULL , `Phone` TEXT NOT NULL ) ENGINE = InnoDB; ");
        DatabaseConnection.checkTable("ArhieveStudent", "CREATE TABLE `library`.`arhieveStudent` ( `StudentID` TEXT NOT NULL , `Name` TEXT NOT NULL , `Email` TEXT NOT NULL , `Phone` TEXT NOT NULL ) ENGINE = InnoDB; ");
        DatabaseConnection.checkTable("IssueBook", "CREATE TABLE `library`.`issuebook` ( `IssuedID` INT NOT NULL AUTO_INCREMENT , `BookID` TEXT NOT NULL , `BookName` TEXT NOT NULL , `StudentID` TEXT NOT NULL , `StudentName` TEXT NOT NULL , `IssuedDate` TEXT NOT NULL , `ReturnDate` TEXT NOT NULL , `Days` INT NOT NULL , `Fee` DOUBLE NULL , `Notify` TEXT NOT NULL DEFAULT 'False' , PRIMARY KEY (`IssuedID`)) ENGINE = InnoDB;");
        DatabaseConnection.checkTable("ShortTermBook", "CREATE TABLE `library`.`shorttermbook` ( `IssuedID` INT NOT NULL AUTO_INCREMENT , `BookID` TEXT NOT NULL , `BookName` TEXT NOT NULL , `StudentID` TEXT NOT NULL , `StudentName` TEXT NOT NULL , `IssuedTime` TEXT NOT NULL , `ReturnTime` TEXT NOT NULL , `Hours` INT NOT NULL , `Fee` DOUBLE NULL , `Notify` TEXT NOT NULL DEFAULT 'False' , PRIMARY KEY (`IssuedID`)) ENGINE = InnoDB;");
        DatabaseConnection.checkTable("Account", "CREATE TABLE `library`.`account` ( `ID` INT NOT NULL DEFAULT '1' , `LateFeePerDay` DOUBLE NOT NULL , `LateFeePerHour` DOUBLE NOT NULL ) ENGINE = InnoDB; ");
        DatabaseConnection.checkTable("MailServer", "CREATE TABLE `library`.`mailserver` ( `ServerName` TEXT NOT NULL , `ServerPort` INT NOT NULL , `Email` TEXT NOT NULL , `Password` TEXT NOT NULL , `Authentication` TEXT NOT NULL , `TLS` TEXT NOT NULL ) ENGINE = InnoDB; ");
        Tooltip closeApp = new Tooltip("Close");
        closeApp.setStyle("-fx-font-size:11");
        closeApp.setMinSize(20, 20);
        close.setTooltip(closeApp);
        FadeTransition fadeinText = new FadeTransition(Duration.seconds(1), text);
        fadeinText.setToValue(1);

        TranslateTransition moveText = new TranslateTransition(Duration.seconds(1), text);
        moveText.setToX(80);
        moveText.play();
        moveText.setOnFinished((e) -> {
            fadeinText.play();
        });

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
}

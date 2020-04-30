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
        DatabaseConnection.checkTable("User", "CREATE TABLE `User` ( `ID` INTEGER PRIMARY KEY AUTOINCREMENT, `Firstname` TEXT, `Lastame` TEXT, `Username` TEXT, `Email` TEXT,`Password` TEXT, `Usertype` TEXT )");
        DatabaseConnection.checkTable("Fee", "CREATE TABLE `Fee` (`BookID` TEXT, `BookName` TEXT, `StudentID` TEXT, `StudentName ` TEXT,`BorrowedDate` TEXT, `ReturnDate` TEXT,`LateFee` Double, `Username` TEXT,`Date` TEXT )");
        DatabaseConnection.checkTable("Book", "CREATE TABLE Book ('BookID' TEXT, 'Name' TEXT, 'Author' TEXT, 'Publisher' TEXT, 'Edition' INTEGER, 'Quantity' INTEGER,'RemainingBooks' INTEGER Default 0, 'Availability' TEXT Default 'Available', 'Section' TEXT,PRIMARY KEY(BookID))");
        DatabaseConnection.checkTable("Student", "CREATE TABLE Student ( `StudentID` TEXT, `Name` TEXT, `Email` TEXT, `Phone` TEXT, PRIMARY KEY(StudentID))");
        DatabaseConnection.checkTable("ArhieveStudent", "CREATE TABLE ArhieveStudent ( `StudentID` TEXT, `Name` TEXT, `Email` TEXT, `Phone` TEXT, PRIMARY KEY(StudentID))");
        DatabaseConnection.checkTable("IssueBook", "CREATE TABLE \"IssueBook\" ( `IssuedID` INTEGER PRIMARY KEY AUTOINCREMENT, `BookID` TEXT, `BookName` TEXT, `StudentID` TEXT, `StudentName` TEXT, `IssuedDate` Text, `ReturnDate` Text, `Days` Integer, `Fee` Double DEFAULT 0.00,`Notify` Text Default 'False',FOREIGN KEY(`BookID`) REFERENCES `Book`(`BookID`), FOREIGN KEY(`StudentID`) REFERENCES `Student`(`StudentID`) )");
        DatabaseConnection.checkTable("ShortTermBook", "CREATE TABLE \"ShortTermBook\" ( `IssuedID` INTEGER PRIMARY KEY AUTOINCREMENT, `BookID` TEXT, `BookName` TEXT, `StudentID` TEXT, `StudentName` TEXT, `IssuedTime` Text, `ReturnTime` Text, `Hours` Integer, `Fee` Double DEFAULT 0.00,`Notify` Text Default 'False',FOREIGN KEY(`BookID`) REFERENCES `Book`(`BookID`), FOREIGN KEY(`StudentID`) REFERENCES `Student`(`StudentID`) )");
        DatabaseConnection.checkTable("Account", "CREATE TABLE Account ( `ID` Integer default 1,`LateFeePerDay` Double,`LateFeePerHour` Double)");
        DatabaseConnection.checkTable("MailServer", "CREATE TABLE `MailServer` ( `ServerName` TEXT, `ServerPort` INTEGER, `Email` TEXT, `Password` TEXT, `Authentication` TEXT, `TLS` TEXT )");

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

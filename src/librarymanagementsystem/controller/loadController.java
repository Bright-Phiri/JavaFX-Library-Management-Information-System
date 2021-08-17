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

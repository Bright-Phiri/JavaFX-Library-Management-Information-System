/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.model;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Bright
 */
public class LoadStage {

    public LoadStage(String url, Node node) {
        try {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Parent root = FXMLLoader.load(getClass().getResource(url));
            Scene scene;
            Stage stage = (Stage) node.getScene().getWindow();

            if (url.equals("/librarymanagementsystem/view/createAccount.fxml")
                    || url.equals("/librarymanagementsystem/view/login.fxml")
                    || url.equals("/librarymanagementsystem/view/forgetPassword.fxml")) {

                stage.hide();
                Stage stage1 = new Stage();
                scene = new Scene(root);
                stage1.setScene(scene);
                stage1.initStyle(StageStyle.UNDECORATED);
                stage1.centerOnScreen();
                stage1.show();
            } else {
                // Create a scene with the full screen dimensions
                scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.setX(screenBounds.getMinX());
                stage.setY(screenBounds.getMinY());
                stage.setWidth(screenBounds.getWidth());
                stage.setHeight(screenBounds.getHeight());
                stage.show();

                // Add listeners to handle dynamic resizing for the else block
                stage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                    double scaleFactor = newWidth.doubleValue() / screenBounds.getWidth();
                    root.setScaleX(scaleFactor);
                });

                stage.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                    double scaleFactor = newHeight.doubleValue() / screenBounds.getHeight();
                    root.setScaleY(scaleFactor);
                });
            }
        } catch (IOException ex) {
            Logger.getLogger(LoadStage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

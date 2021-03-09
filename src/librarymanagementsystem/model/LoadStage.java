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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Bright
 */
public class LoadStage {

    public LoadStage(String url, Node node) {
        Scene scene = null;
        try {
            Parent root = FXMLLoader.load(getClass().getResource(url));
            if (url.equals("/app/view/createAccount.fxml") || url.equals("/app/view/login.fxml") || url.equals("/app/view/forgetPassword.fxml")) {
                scene = new Scene(root);
            } else {
                scene = new Scene(root, 1283, 680);
            }
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoadStage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

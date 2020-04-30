
package librarymanagementsystem.model;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Bright
 */
public class LibraryManagementSystem extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
           Parent root = FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/load.fxml"));
           Scene scene = new Scene(root);
           stage.setScene(scene);
           stage.getIcons().add(new Image("/librarymanagementsystem/images/books.png"));
           stage.initStyle(StageStyle.UNDECORATED);
           stage.show();
           
        Task<Void> task = new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                    Thread.sleep(3000);
                       Platform.runLater(new Runnable() {
                           @Override
                           public void run() {
                                  try {
                                     Parent root2 = FXMLLoader.load(getClass().getResource("/librarymanagementsystem/view/login.fxml"));
                                     Scene scene2 = new Scene(root2);
                                     try {
                                         Thread.sleep(50);
                                     } catch (InterruptedException ex) {
                                       Logger.getLogger(LibraryManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                                     }
                                     stage.setScene(scene2);
                                     stage.show();
                                   } catch (IOException ex) {
                                       Logger.getLogger(LibraryManagementSystem.class.getName()).log(Level.SEVERE, null, ex);
                                   }
                           }
                       });
                   return null;  
               }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

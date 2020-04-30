/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.model;

/**
 *
 * @author Bright
 */
public class Alert {
       public Alert(javafx.scene.control.Alert.AlertType type,String title,String text){
              javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
              alert.setTitle(title);
              alert.setHeaderText(null);
              alert.setContentText(text);
              alert.showAndWait();
       }
}

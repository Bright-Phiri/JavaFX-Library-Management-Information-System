/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Bright
 */
public class Librarian {
       private SimpleIntegerProperty id;
       private SimpleStringProperty firstName;
       private SimpleStringProperty lastName;
       private SimpleStringProperty userName;
       private SimpleStringProperty emailAddress;
       private SimpleStringProperty password;
       private SimpleStringProperty status;
       
       public Librarian(int id,String firstName,String lastName,String userName,String emailAddress,String password){
              this.id = new SimpleIntegerProperty(id);
              this.firstName = new SimpleStringProperty(firstName);
              this.lastName = new SimpleStringProperty(lastName);
              this.userName = new SimpleStringProperty(userName);
              this.emailAddress = new SimpleStringProperty(emailAddress);
              this.password = new SimpleStringProperty(password);
       }
       
       public Librarian(String firstName,String lastName,String userName,String emailAddress,String password){
              this.firstName = new SimpleStringProperty(firstName);
              this.lastName = new SimpleStringProperty(lastName);
              this.userName = new SimpleStringProperty(userName);
              this.emailAddress = new SimpleStringProperty(emailAddress);
              this.password = new SimpleStringProperty(password);
              
       }

     
       public int getId() {
              return id.get();
       }

       public String getFirstName() {
              return firstName.get();
       }

       public String getLastName() {
              return lastName.get();
       }

       public String getUserName() {
              return userName.get();
       }

       public String getEmailAddress() {
              return emailAddress.get();
       }

      public String getPassword() {
             return password.get();
      } 
}

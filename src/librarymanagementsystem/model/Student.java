/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

/**
 *
 * @author Bright
 */
public class Student {

    private final CheckBox check;
    private final SimpleStringProperty studentID;
    private final SimpleStringProperty studentName;
    private final SimpleStringProperty studentEmail;
    private final SimpleStringProperty studentPhone;

    public Student(String studentID, String studentName, String studentEmail, String studentPhone) {
        this.check = new CheckBox();
        this.studentID = new SimpleStringProperty(studentID);
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.studentPhone = new SimpleStringProperty(studentPhone);
    }
    
    public CheckBox getCheck() {
        return check;
    }

    public String getStudentID() {
        return studentID.get();
    }

    public String getStudentName() {
        return studentName.get();
    }

    public String getStudentEmail() {
        return studentEmail.get();
    }

    public String getStudentPhone() {
        return studentPhone.get();
    }
}

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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class IssuedBook {

    private SimpleIntegerProperty issuedID;
    private SimpleStringProperty bookID;
    private SimpleStringProperty bookName;
    private SimpleStringProperty studentID;
    private SimpleStringProperty studentName;
    private SimpleStringProperty issuedTime;
    private SimpleStringProperty returnDate;
    private SimpleIntegerProperty days;
    private SimpleDoubleProperty fee;

    public IssuedBook(int issuedID, String bookID, String bookName, String studentID, String studentName, String issuedTime, String returnDate, int days, double fee) {
        this.issuedID = new SimpleIntegerProperty(issuedID);
        this.bookID = new SimpleStringProperty(bookID);
        this.bookName = new SimpleStringProperty(bookName);
        this.studentID = new SimpleStringProperty(studentID);
        this.studentName = new SimpleStringProperty(studentName);
        this.issuedTime = new SimpleStringProperty(issuedTime);
        this.returnDate = new SimpleStringProperty(returnDate);
        this.days = new SimpleIntegerProperty(days);
        this.fee = new SimpleDoubleProperty(fee);
    }

    public IssuedBook(String studentID, String studenName, String bookName, String issuedDayorTime, String returnDateOrTime) {
        this.studentID = new SimpleStringProperty(studentID);
        this.studentName = new SimpleStringProperty(studenName);
        this.bookName = new SimpleStringProperty(bookName);
        this.issuedTime = new SimpleStringProperty(issuedDayorTime);
        this.returnDate = new SimpleStringProperty(returnDateOrTime);
    }

    public int getIssuedID() {
        return issuedID.get();
    }

    public String getBookID() {
        return bookID.get();
    }

    public String getStudentID() {
        return studentID.get();
    }

    public String getIssuedTime() {
        return issuedTime.get();
    }

    public String getBookName() {
        return bookName.get();
    }

    public String getStudentName() {
        return studentName.get();
    }

    public String getReturnDate() {
        return returnDate.get();
    }

    public int getDays() {
        return days.get();
    }

    public double getFee() {
        return fee.get();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Bright
 */
public class IssueDetails {

    private final SimpleStringProperty bookId;
    private final SimpleStringProperty bookName;
    private final SimpleStringProperty studentId;
    private final SimpleStringProperty studentName;
    private final SimpleStringProperty borrowedDate;
    private final SimpleStringProperty returnDate;
    private final SimpleDoubleProperty fee;
    private final SimpleStringProperty user;

    public IssueDetails(String bookId, String bookName, String studentId, String studentName, String borrowedDate, String returnDate, double fee, String user) {
        this.bookId = new SimpleStringProperty(bookId);
        this.bookName = new SimpleStringProperty(bookName);
        this.studentId = new SimpleStringProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.borrowedDate = new SimpleStringProperty(borrowedDate);
        this.returnDate = new SimpleStringProperty(returnDate);
        this.fee = new SimpleDoubleProperty(fee);
        this.user = new SimpleStringProperty(user);
    }

    public String getBookId() {
        return bookId.get();
    }

    public String getBookName() {
        return bookName.get();
    }

    public String getStudentId() {
        return studentId.get();
    }

    public String getStudentName() {
        return studentName.get();
    }

    public String getBorrowedDate() {
        return borrowedDate.get();
    }

    public String getReturnDate() {
        return returnDate.get();
    }

    public double getFee() {
        return fee.get();
    }

    public String getUser() {
        return user.get();
    }
}

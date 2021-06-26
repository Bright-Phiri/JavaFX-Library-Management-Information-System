/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

/**
 *
 * @author Bright
 */
public class Book {

    private final CheckBox check;
    private final SimpleStringProperty bookID;
    private final SimpleStringProperty bookName;
    private final SimpleStringProperty bookAuthor;
    private final SimpleStringProperty bookPublisher;
    private final SimpleIntegerProperty bookEdition;
    private final SimpleIntegerProperty bookQuantity;
    private final SimpleIntegerProperty remainingBooks;
    private final SimpleStringProperty availability;
    private final SimpleStringProperty bookSection;

    public Book(String bookID, String bookName, String bookAuthor, String bookPublisher, int bookEdition, int bookQauntity, int remainingBooks, String availability, String section) {
        this.check = new CheckBox();
        this.bookID = new SimpleStringProperty(bookID);
        this.bookName = new SimpleStringProperty(bookName);
        this.bookAuthor = new SimpleStringProperty(bookAuthor);
        this.bookPublisher = new SimpleStringProperty(bookPublisher);
        this.bookEdition = new SimpleIntegerProperty(bookEdition);
        this.bookQuantity = new SimpleIntegerProperty(bookQauntity);
        this.remainingBooks = new SimpleIntegerProperty(remainingBooks);
        this.availability = new SimpleStringProperty(availability);
        this.bookSection = new SimpleStringProperty(section);
    }

    public CheckBox getCheck() {
        return check;
    }

    public String getBookID() {
        return bookID.get();
    }

    public String getBookName() {
        return bookName.get();
    }

    public String getBookAuthor() {
        return bookAuthor.get();
    }

    public String getBookPublisher() {
        return bookPublisher.get();
    }

    public int getBookEdition() {
        return bookEdition.get();
    }

    public int getBookQuantity() {
        return bookQuantity.get();
    }

    public int getRemainingBooks() {
        return remainingBooks.get();
    }

    public String getAvailability() {
        return availability.get();
    }

    public String getBookSection() {
        return bookSection.get();
    }

}

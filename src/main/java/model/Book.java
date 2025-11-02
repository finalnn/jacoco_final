package model;

import java.time.LocalDate;

/**
 * Represents a book in the library system.
 * Keeps track of borrowing status and due date.
 */
public class Book {
    private final String title;
    private final String author;
    private final String isbn;
    private boolean borrowed = false;
    private LocalDate dueDate = null;

    /**
     * Constructs a book with title, author, and ISBN.
     * @param title The title of the book.
     * @param author The author of the book.
     * @param isbn The ISBN identifier.
     */
    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isBorrowed() { return borrowed; }
    public LocalDate getDueDate() { return dueDate; }

    /**
     * Marks the book as borrowed and sets due date to 28 days from today.
     */
    public void borrow() {
        borrowed = true;
        dueDate = LocalDate.now().plusDays(28);
    }

    /**
     * Marks the book as returned and clears due date.
     */
    public void returnBook() {
        borrowed = false;
        dueDate = null;
    }

    /**
     * Checks if the book is overdue.
     * @return true if overdue, false otherwise.
     */
    public boolean isOverdue() {
        return borrowed && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        String status = borrowed ? "(Borrowed, due: " + dueDate + ")" : "(Available)";
        return title + " by " + author + " (ISBN: " + isbn + ") " + status;
    }
}

package model;

import java.time.LocalDate;

/**
 * Represents a loan of a book to a user.
 */
public class Loan {
    private final Book book;
    private final User user;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private boolean returned = false;

    /**
     * Creates a new loan for a book and user.
     * @param book The book being borrowed.
     * @param user The user borrowing the book.
     */
    public Loan(Book book, User user) {
        this.book = book;
        this.user = user;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(28); // 28-day loan period
        this.book.borrow();
    }

    public Book getBook() { return book; }
    public User getUser() { return user; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return returned; }

    /**
     * Marks the loan as returned.
     */
    public void markReturned() {
        returned = true;
        book.returnBook();
    }

    /**
     * Checks if the loan is overdue.
     * @return true if overdue, false otherwise.
     */
    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(dueDate);
    }
}

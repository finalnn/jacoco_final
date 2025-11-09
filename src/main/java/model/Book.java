package model;

import java.time.LocalDate;

public class Book {
    private final String title;
    private final String author;
    private final String isbn;
    private boolean borrowed = false;
    private LocalDate dueDate = null;
    private User borrower = null;

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
    public User getBorrower() { return borrower; }

    public void borrow(User user) {
        borrowed = true;
        borrower = user;
        dueDate = LocalDate.now().plusDays(28);
    }

    public void returnBook() {
        borrowed = false;
        borrower = null;
        dueDate = null;
    }

    public boolean isOverdue() {
        return borrowed && LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (isOverdue()) {
            return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }
        return 0;
    }
    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", ISBN: " + isbn;
    }

}

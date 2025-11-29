package model;

import java.time.LocalDate;

public class Loan {
    private final Book book;
    private final User user;
    private final LocalDate borrowDate;
    private LocalDate dueDate;  // غيرنا final لتسهيل التعديل
    private boolean returned = false;

    
    
    public Loan(Book book, User user) {
        this.book = book;
        this.user = user;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(28); // 28 يوم استعارة
        this.book.borrow(user);
    }

    public Book getBook() { return book; }
    public User getUser() { return user; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return returned; }

    // Setter جديد لتاريخ الاستحقاق
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void markReturned() {
        returned = true;
        book.returnBook();
    }

    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(dueDate);
    }
}

package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    private final Media media;
    private final User user;
    private final LocalDate borrowDate; // تاريخ الاستعارة
    private LocalDate dueDate;
    private boolean returned = false;

    public Loan(Media media, User user) {
        this.media = media;
        this.user = user;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(
            media instanceof Book ? 28 : 7
        );

        media.borrow(user);
        user.addLoan(this);
    }

    public Media getMedia() { return media; }
    public User getUser() { return user; }
    public LocalDate getBorrowDate() { return borrowDate; } // ✅ هنا أضفنا getter
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return returned; }

    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (isOverdue()) return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return 0;
    }

    public double getFineAmount() {
        return getDaysOverdue() * media.getFinePerDay();
    }

    public void markReturned() {
        returned = true;
        media.returnMedia();
    }

    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; } 
}

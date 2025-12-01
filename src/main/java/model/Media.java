package model;

import java.time.LocalDate;

public interface Media {
    String getTitle();
    boolean isBorrowed();
    void borrow(User user);
    void returnMedia();
    boolean isOverdue();
    long getDaysOverdue();
    User getBorrower();
    double getFinePerDay();
    
    void setDueDate(LocalDate dueDate);
    LocalDate getDueDate();
}

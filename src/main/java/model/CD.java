package model;

import java.time.LocalDate;

public class CD  implements Media {
    private final String title;
    private final String artist;
    private boolean borrowed = false;
    private LocalDate dueDate = null;
    private User borrower = null;
    private String id;
    private double fine;

    public CD(String title, String artist, String id) {
        this.title = title;
        this.artist = artist;
        this.id = id;
        this.borrowed = false;
        this.fine = 0;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public boolean isBorrowed() { return borrowed; }
    public LocalDate getDueDate() { return dueDate; }
    public User getBorrower() { return borrower; }

    @Override
    public void borrow(User user) {
        borrowed = true;
        borrower = user;
        dueDate = LocalDate.now().plusDays(7); // US5.1: 7 أيام
    }

    @Override
    public double getFinePerDay() {
        return 20.0; // NIS
    }


    public void returnCD() {
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
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    @Override
    public void returnMedia() {
        returnCD(); // نستدعي الطريقة القديمة
    }
    @Override
    public String toString() {
        return "CD: " + title + ", Artist: " + artist;
    }
}

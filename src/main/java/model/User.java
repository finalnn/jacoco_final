package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String name;
    private final String email;
    private double fineBalance = 0.0;

    private final List<Loan> loans = new ArrayList<>();

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public double getFineBalance() { return fineBalance; }

    public void addFine(double amount) {
        fineBalance += amount;
    }

    public void payFine(double amount) {
        if (amount >= fineBalance) {
            fineBalance = 0;
        } else {
            fineBalance -= amount;
        }
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public boolean hasOverdueBooks() {
        for (Loan loan : loans) {
            if (loan.isOverdue()) {
                return true;
            }
        }
        return false;
    }

    public boolean canBorrow() {
        // شرط 1: لا غرامات غير مدفوعة
        if (fineBalance > 0) {
            return false;
        }

        // شرط 2: لا كتب متأخرة
        if (hasOverdueBooks()) {
            return false;
        }

        return true;
    }
}

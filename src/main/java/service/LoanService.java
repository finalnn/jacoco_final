package service;

import model.Book;
import model.Loan;
import model.User;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class LoanService {

    private final List<Loan> loans = new ArrayList<>();

    public Loan createLoan(Book book, User user) {
        if (!canBorrow(user)) {
            long daysOverdue = loans.stream()
                    .filter(l -> l.getUser() == user && !l.isReturned() && l.isOverdue())
                    .mapToLong(l -> ChronoUnit.DAYS.between(l.getDueDate(), LocalDate.now()))
                    .sum();
            throw new IllegalStateException(
                    "You cannot borrow a new book. You have unpaid fines or overdue books. Total overdue days: " + daysOverdue
            );
        }

        Loan loan = new Loan(book, user);
        loans.add(loan);
        return loan;
    }

    public void returnLoan(Loan loan) {
        if (loan.isOverdue()) {
            long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
            double fineAmount = daysOverdue * 1.0; // 1$ لكل يوم متأخر
            loan.getUser().addFine(fineAmount);
        }
        loan.markReturned();
    }

    public boolean canBorrow(User user) {
        boolean hasFines = user.getFineBalance() > 0;
        boolean hasOverdue = loans.stream()
                .anyMatch(l -> l.getUser() == user && !l.isReturned() && l.isOverdue());
        return !hasFines && !hasOverdue;
    }

    public List<Loan> getAllLoans() {
        return loans;
    }

    public List<Loan> getUserLoans(User user) {
        List<Loan> res = new ArrayList<>();
        for (Loan l : loans) {
            if (l.getUser() == user && !l.isReturned()) res.add(l);
        }
        return res;
    }
}

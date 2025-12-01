package service;

import model.Loan;
import model.Media;
import model.User;

import java.util.ArrayList;
import java.util.List;

public class LoanService {
    private final List<Loan> loans = new ArrayList<>();

    public Loan createLoan(Media media, User user) {
        if (!user.canBorrow()) {
            throw new IllegalStateException("You cannot borrow new media.");
        }
        Loan loan = new Loan(media, user);
        loans.add(loan);
        user.addLoan(loan);
        return loan;
    }

    public List<Loan> getUserLoans(User user) {
        return loans.stream()
                    .filter(l -> l.getUser() == user && !l.isReturned())
                    .toList();
    }

    public void returnLoan(Loan loan) {
        if (loan.isOverdue()) {
            double fine = loan.getFineAmount();
            loan.getUser().addFine(fine);
        }
        loan.markReturned();
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }
}

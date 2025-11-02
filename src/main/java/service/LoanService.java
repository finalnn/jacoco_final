package service;

import model.Book;
import model.Loan;
import model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage loans.
 */
public class LoanService {
    private final List<Loan> loans = new ArrayList<>();

    public Loan createLoan(Book book, User user) {
        Loan loan = new Loan(book, user);
        loans.add(loan);
        return loan;
    }

    public void returnLoan(Loan loan) {
        loan.markReturned();
    }

    public List<Loan> getAllLoans() { return loans; }

    public List<Loan> getUserLoans(User user) {
        List<Loan> result = new ArrayList<>();
        for (Loan l : loans) {
            if (l.getUser().equals(user) && !l.isReturned()) result.add(l);
        }
        return result;
    }
}

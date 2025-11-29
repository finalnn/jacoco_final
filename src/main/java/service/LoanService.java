package service;

import model.Loan;
import model.Book;
import model.User;
import java.util.ArrayList;
import java.util.List;

public class LoanService {

    private final List<Loan> loans = new ArrayList<>();
    private final FineService fineService;

    // كونستركتور جديد
    public LoanService(FineService fineService) {
        this.fineService = fineService;
    }

    public Loan createLoan(Book book, User user) {
        Loan loan = new Loan(book, user);
        loans.add(loan);
        return loan;
    }

    public void returnLoan(Loan loan) {
        if (loan.isOverdue()) {
            fineService.addFine(loan.getUser(), 5.0); // قيمة الغرامة الثابتة
        }
        loan.markReturned();
    }

    public List<Loan> getAllLoans() {
        return loans;
    }

    public List<Loan> getUserLoans(User user) {
        List<Loan> res = new ArrayList<>();
        for (Loan l : loans) {
            if (l.getUser() == user && !l.isReturned()) {
                res.add(l);
            }
        }
        return res;
    }
}

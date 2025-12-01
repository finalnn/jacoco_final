package service;

import model.User;
import model.Loan;

public class UserService {

    public void addFine(User user, double amount) {
        user.addFine(amount);
    }

    public void payFine(User user, double amount) {
        user.payFine(amount);
    }

    public boolean canBorrow(User user) {
        return user.canBorrow();
    }

    public String getBorrowStatus(User user) {
        if (user.getFineBalance() > 0) {
            return "You cannot borrow a new book because you have unpaid fines.";
        }
        if (userHasOverdueBooks(user)) {
            return "You cannot borrow a new book because you have overdue books.";
        }
        return "You can borrow books.";
    }

    private boolean userHasOverdueBooks(User user) {
        for (Loan l : user.getLoans()) {
            if (l.isOverdue()) return true;
        }
        return false;
    }
}

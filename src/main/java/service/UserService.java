package service;

import model.User;

/**
 * Service to manage users and their fines.
 */
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
}

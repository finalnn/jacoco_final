package service;

import model.Admin;
import model.User;
import model.Loan;
import exception.AuthenticationException;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    private final Admin admin;
    private boolean loggedIn = false;
    private final static List<User> users = new ArrayList<>();

    public AdminService(Admin admin) {
        this.admin = admin;
    }

    public void login(String username, String password) {
        if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
            loggedIn = true;
            System.out.println("Login successful! Welcome, admin.");
        } else {
            throw new AuthenticationException("Invalid credentials!");
        }
    }

    public void logout() {
        if (loggedIn) loggedIn = false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void addUser(User user) {
        if (!loggedIn) return;
        users.add(user);
    }

    public void unregisterUser(User user) {
        if (!loggedIn) {
            System.out.println("Access denied! Please log in as admin first.");
            return;
        }
        if (user.getFineBalance() > 0) {
            System.out.println("Cannot unregister user with unpaid fines.");
            return;
        }
        boolean hasActiveLoan = user.getLoans().stream().anyMatch(l -> !l.isReturned());
        if (hasActiveLoan) {
            System.out.println("Cannot unregister user with active loans.");
            return;
        }
        users.remove(user);
        System.out.println("User " + user.getName() + " unregistered successfully.");
    }

    public static List<User> getAllUsers() {
        return users;
    }
}

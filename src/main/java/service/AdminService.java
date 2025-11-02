package service;

import model.Admin;
import exception.AuthenticationException;

public class AdminService {
    private final Admin admin;
    private boolean loggedIn = false; // حالة الجلسة

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
        if (loggedIn) {
            loggedIn = false;
            System.out.println("Logout successful. Session closed securely.");
        } else {
            System.out.println("No active session to logout from.");
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}

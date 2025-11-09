package service;

import model.Admin;
import model.User;
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

    public void addUser(User user) {
        if (!loggedIn) {
            System.out.println("Access denied! Please log in as admin first.");
            return;
        }
        users.add(user);
        System.out.println("User added successfully: " + user.getName());
    }

    public static List<User> getAllUsers() {
        return users;
    }
}

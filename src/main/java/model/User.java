package model;

public class User {
    private final String name;
    private final String email;
    private double fineBalance = 0.0;

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
        if (amount > fineBalance) {
            fineBalance = 0;
        } else {
            fineBalance -= amount;
        }
    }

    public boolean canBorrow() {
        return fineBalance <= 0;
    }
}

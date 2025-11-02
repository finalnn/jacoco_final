package model;

/**
 * Represents a library user with fine balance.
 */
public class User {
    private final String name;
    private double fineBalance = 0.0;

    /**
     * Constructs a user with a name.
     * @param name The user's name.
     */
    public User(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public double getFineBalance() { return fineBalance; }

    /**
     * Adds a fine to the user's account.
     * @param amount Fine amount to add.
     */
    public void addFine(double amount) {
        fineBalance += amount;
    }

    /**
     * Pays a portion or full fine.
     * @param amount Amount to pay.
     */
    public void payFine(double amount) {
        if (amount > fineBalance) {
            fineBalance = 0;
        } else {
            fineBalance -= amount;
        }
    }

    /**
     * Checks if the user can borrow books (no outstanding fines).
     * @return true if allowed to borrow.
     */
    public boolean canBorrow() {
        return fineBalance <= 0;
    }
}

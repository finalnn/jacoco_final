package model;

/**
 * Represents a fine applied to a user.
 */
public class Fine {
    private final User user;
    private final double amount;
    private boolean paid = false;

    /**
     * Constructs a fine for a user.
     * @param user The user who receives the fine.
     * @param amount The fine amount.
     */
    public Fine(User user, double amount) {
        this.user = user;
        this.amount = amount;
        user.addFine(amount);
    }

    public User getUser() { return user; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return paid; }

    /**
     * Pays the fine.
     */
    public void pay() {
        if (!paid) {
            paid = true;
            user.payFine(amount);
        }
    }
}

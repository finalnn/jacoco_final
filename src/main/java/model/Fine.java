package model;
public class Fine {
    private final User user;
    private final double amount;
    private boolean paid = false;

    public Fine(User user, double amount) {
        this.user = user;
        this.amount = amount;
        user.addFine(amount);
    }

    public User getUser() { return user; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return paid; }

    public void pay() {
        if (!paid) {
            paid = true;
            user.payFine(amount);
        }
    }
}

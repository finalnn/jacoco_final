package service;

import model.Fine;
import model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage fines.
 */
public class FineService {
    private final List<Fine> fines = new ArrayList<>();

    public Fine addFine(User user, double amount) {
        Fine fine = new Fine(user, amount);
        fines.add(fine);
        return fine;
    }

    public void payFine(Fine fine) {
        fine.pay();
    }

    public List<Fine> getAllFines() { return fines; }

    public List<Fine> getUserFines(User user) {
        List<Fine> result = new ArrayList<>();
        for (Fine f : fines) {
            if (f.getUser().equals(user) && !f.isPaid()) result.add(f);
        }
        return result;
    }
}

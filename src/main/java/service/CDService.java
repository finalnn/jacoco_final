package service;

import model.CD;
import model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class CDService extends Observable {

    private final List<CD> cds = new ArrayList<>();
    private SearchStrategy searchStrategy;

    public void addCD(String title, String artist, String id) {
        boolean exists = cds.stream().anyMatch(c -> c.getId().equals(id));
        if (!exists) {
            cds.add(new CD(title, artist, id));
        }
    }

    public void addCD(CD cd) {
        boolean exists = cds.stream().anyMatch(c -> c.getId().equals(cd.getId()));
        if (!exists) {
            cds.add(cd);
        }
    }

    public List<CD> getAllCDs() {
        return cds;
    }

    public void setSearchStrategy(SearchStrategy strategy) {
        this.searchStrategy = strategy;
    }
    public List<CD> search(String query) {
        List<CD> result = new ArrayList<>();
        String q = query.toLowerCase();

        for (CD cd : cds) {
            if (cd.getTitle().toLowerCase().contains(q) ||
                cd.getArtist().toLowerCase().contains(q) ||
                cd.getId().toLowerCase().contains(q)) 
            {
                result.add(cd);
            }
        }

        return result;
    }

    public boolean borrowCD(CD cd, User user) {
        if (!user.canBorrow()) return false;
        if (!cd.isBorrowed()) {
            cd.borrow(user);
            return true;
        }
        return false;
    }

    public void returnCD(CD cd, User user) {
        if (cd.isBorrowed()) {
            if (cd.isOverdue()) user.addFine(cd.getFinePerDay());
            cd.returnMedia();
        }
    }

    public void checkOverdueCDs() {
        for (CD cd : cds) {
            if (cd.isBorrowed() && cd.isOverdue()) {
                setChanged();
                notifyObservers(cd);
            }
        }
    }
}
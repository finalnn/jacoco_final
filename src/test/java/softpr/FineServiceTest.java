package softpr;

import model.Fine;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FineService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FineServiceTest {

    private FineService fineService;
    private User user;

    @BeforeEach
    public void setup() {
        fineService = new FineService();
        user = new User("Noor");
    }

    @Test
    public void addFineIncreasesUserBalance() {
        Fine fine = fineService.addFine(user, 5);
        assertEquals(5, user.getFineBalance());
        assertFalse(fine.isPaid());
    }

    @Test
    public void payFineSetsPaidAndUpdatesUserBalance() {
        Fine fine = fineService.addFine(user, 10);
        fineService.payFine(fine);
        assertTrue(fine.isPaid());
        assertEquals(0, user.getFineBalance());
    }

    @Test
    public void getUserFinesReturnsOnlyUnpaid() {
        fineService.addFine(user, 5);
        Fine fine2 = fineService.addFine(user, 10);
        fineService.payFine(fine2);
        List<Fine> unpaid = fineService.getUserFines(user);
        assertEquals(1, unpaid.size());
    }

    @Test
    public void getAllFinesReturnsAll() {
        fineService.addFine(user, 5);
        fineService.addFine(user, 10);
        List<Fine> all = fineService.getAllFines();
        assertEquals(2, all.size());
    }
}

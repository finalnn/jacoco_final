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
        user = new User("Noor","noorfayek321@gmail.com");
    }

    @Test
    public void fineGetAmountShouldReturnCorrectValue() {
        Fine fine = fineService.addFine(user, 15);
        assertEquals(15, fine.getAmount());
    }

    @Test
    public void addFineShouldIncreaseUserBalanceAndAddFine() {
        Fine fine = fineService.addFine(user, 5);
        assertEquals(5, user.getFineBalance());
        assertFalse(fine.isPaid());
        assertTrue(fineService.getAllFines().contains(fine));
    }

    @Test
    public void payFineShouldSetPaidAndResetBalance() {
        Fine fine = fineService.addFine(user, 10);
        fineService.payFine(fine);
        assertTrue(fine.isPaid());
        assertEquals(0, user.getFineBalance());
    }

    @Test
    public void getUserFinesShouldReturnOnlyUnpaidFines() {
        Fine fine1 = fineService.addFine(user, 5);
        Fine fine2 = fineService.addFine(user, 10);
        fineService.payFine(fine2);
        List<Fine> unpaid = fineService.getUserFines(user);
        assertEquals(1, unpaid.size());
        assertTrue(unpaid.contains(fine1));
    }

    @Test
    public void getAllFinesShouldReturnAllFines() {
        fineService.addFine(user, 5);
        fineService.addFine(user, 10);
        List<Fine> fines = fineService.getAllFines();
        assertEquals(2, fines.size());
    }

    @Test
    public void payFineShouldNotAffectOtherUsers() {
        User user2 = new User("Mohammad", "noorfayek2018@gmail.com");
        Fine fine1 = fineService.addFine(user, 5);
        Fine fine2 = fineService.addFine(user2, 5);
        fineService.payFine(fine1);
        assertEquals(0, user.getFineBalance());
        assertEquals(5, user2.getFineBalance());
        assertFalse(fine2.isPaid());
    }

    @Test
    public void userHasUnpaidFinesDetectsCorrectly() {
        assertFalse(fineService.userHasUnpaidFines(user));
        fineService.addFine(user, 5);
        assertTrue(fineService.userHasUnpaidFines(user));
    }
}

package softpr;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private User user;
    private UserService userService;

    @BeforeEach
    public void setup() {
        user = new User("Noor");
        userService = new UserService();
    }

    @Test
    public void userCanBorrowWhenNoFine() {
        assertTrue(userService.canBorrow(user));
    }

    @Test
    public void userCannotBorrowWhenHasFine() {
        user.addFine(10);
        assertFalse(userService.canBorrow(user));
    }

    @Test
    public void addFineIncreasesBalance() {
        userService.addFine(user, 5);
        assertEquals(5, user.getFineBalance());
    }

    @Test
    public void payFineReducesBalance() {
        user.addFine(10);
        userService.payFine(user, 4);
        assertEquals(6, user.getFineBalance());
    }

    @Test
    public void payFineMoreThanBalanceSetsZero() {
        user.addFine(7);
        userService.payFine(user, 10);
        assertEquals(0, user.getFineBalance());
    }
}

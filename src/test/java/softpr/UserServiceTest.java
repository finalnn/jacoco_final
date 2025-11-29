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
        user = new User("Noor", "noorfayek321@gmail.com");
        userService = new UserService();
    }

    @Test
    public void userCanBorrowWhenNoFine() {
        assertTrue(userService.canBorrow(user));
    }

    @Test
    public void userCannotBorrowWithFine() {
        user.addFine(10);
        assertFalse(userService.canBorrow(user));
    }
    
    @Test
    void testGetNameAndEmail() {
        User user = new User("Noor", "noorfayek321@gmail.com");

        // اختبار getName()
        assertEquals("Noor", user.getName());

        // اختبار getEmail()
        assertEquals("noorfayek321@gmail.com", user.getEmail());
    }

    @Test
    public void payingFineReducesBalance() {
        user.addFine(10);
        userService.payFine(user, 5);
        assertEquals(5, user.getFineBalance());
    }

    @Test
    public void overpayingFineResetsToZero() {
        user.addFine(10);
        userService.payFine(user, 20);
        assertEquals(0, user.getFineBalance());
    }
}

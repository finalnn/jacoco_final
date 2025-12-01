package softpr;

import model.Admin;
import model.User;
import model.Book;
import model.Loan;
import service.AdminService;
import service.LoanService;
import exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceUnitTest {

    private Admin admin;
    private AdminService adminService;
    private User user;
    private LoanService loanService;

    @BeforeEach
    public void setup() {
        admin = new Admin("admin", "1234");
        adminService = new AdminService(admin);
        adminService.login("admin", "1234");
        user = new User("hala", "hala@gmail.com");
        AdminService.getAllUsers().clear();
        adminService.addUser(user);
        loanService = new LoanService();
    }

    
    @Test
    public void validLoginShouldSetIsLoggedInTrue() {
        AdminService as = new AdminService(admin);
        as.login("admin", "1234");
        assertTrue(as.isLoggedIn());
    }

    @Test
    public void invalidLoginThrowsExceptionAndIsLoggedInFalse() {
        AdminService as = new AdminService(admin);
        assertThrows(AuthenticationException.class, () -> as.login("admin", "wrong"));
        assertFalse(as.isLoggedIn());
    }

    @Test
    public void isLoggedInReflectsLogout() {
        AdminService as = new AdminService(admin);
        as.login("admin", "1234");
        assertTrue(as.isLoggedIn());
        as.logout();
        assertFalse(as.isLoggedIn());
    }

    @Test
    public void logoutWhenNotLoggedInDoesNotChangeIsLoggedIn() {
        AdminService as = new AdminService(admin);
        assertFalse(as.isLoggedIn());
        as.logout();
        assertFalse(as.isLoggedIn());
    }

   
    @Test
    public void adminCanUnregisterUserWithNoLoansOrFines() {
        assertTrue(AdminService.getAllUsers().contains(user));
        adminService.unregisterUser(user);
        assertFalse(AdminService.getAllUsers().contains(user));
    }

    @Test
    public void cannotUnregisterUserWithActiveLoan() {
        Book book = new Book("Clean Code", "Robert Martin", "123");
        Loan loan = loanService.createLoan(book, user);
        assertTrue(AdminService.getAllUsers().contains(user));
        adminService.unregisterUser(user);
        assertTrue(AdminService.getAllUsers().contains(user));
    }

    @Test
    public void cannotUnregisterUserWithUnpaidFine() {
        user.addFine(5.0);
        assertTrue(AdminService.getAllUsers().contains(user));
        adminService.unregisterUser(user);
        assertTrue(AdminService.getAllUsers().contains(user));
    }

    @Test
    public void nonLoggedInAdminCannotUnregister() {
        adminService.logout();
        assertTrue(AdminService.getAllUsers().contains(user));
        adminService.unregisterUser(user);
        assertTrue(AdminService.getAllUsers().contains(user));
    }
}

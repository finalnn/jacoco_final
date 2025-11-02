package softpr;
import model.Admin;
import service.AdminService;
import exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceUnitTest {

    private Admin admin;
    private AdminService adminService;

    @BeforeEach
    public void setup() {
        admin = new Admin("admin", "1234");
        adminService = new AdminService(admin);
    }

    @Test
    public void loginWithValidCredentialsShouldSucceed() {
        adminService.login("admin", "1234");
        assertTrue(adminService.isLoggedIn());
    }

    @Test
    public void loginWithInvalidCredentialsShouldFail() {
        assertThrows(AuthenticationException.class, () -> adminService.login("admin", "wrong"));
        assertFalse(adminService.isLoggedIn());
    }

    @Test
    public void logoutShouldSetLoggedInFalse() {
        adminService.login("admin", "1234");
        assertTrue(adminService.isLoggedIn());
        adminService.logout();
        assertFalse(adminService.isLoggedIn());
    }

    @Test
    public void logoutWhenNotLoggedInShouldNotThrow() {
        assertFalse(adminService.isLoggedIn());
        adminService.logout(); // مجرد التأكد أنه لا يحدث خطأ
        assertFalse(adminService.isLoggedIn());
    }
}

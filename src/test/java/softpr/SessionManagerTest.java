package softpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.SessionManager;
import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    public void setup() {
        sessionManager = new SessionManager();
    }

    @Test
    public void sessionCreationIsValid() {
        String token = sessionManager.createSession("Noor");
        assertTrue(sessionManager.isValid(token));
        assertEquals("Noor", sessionManager.getUsername(token));
    }

    @Test
    public void invalidationShouldRemoveSession() {
        String token = sessionManager.createSession("Noor");
        sessionManager.invalidate(token);
        assertFalse(sessionManager.isValid(token));
        assertNull(sessionManager.getUsername(token));
    }

    @Test
    public void invalidSessionReturnsFalse() {
        assertFalse(sessionManager.isValid("badtoken"));
    }
}

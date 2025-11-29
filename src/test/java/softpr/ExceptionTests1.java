package softpr;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import exception.AuthenticationException;
import exception.BookNotAvailableException;
import exception.ValidationException;

class ExceptionTests1 {

    @Test
    void testBookNotAvailableException() {
        BookNotAvailableException ex = assertThrows(BookNotAvailableException.class,
                () -> { throw new BookNotAvailableException("Book not available"); });
        assertEquals("Book not available", ex.getMessage());
    }

    @Test
    void testAuthenticationException() {
        AuthenticationException ex = assertThrows(AuthenticationException.class,
                () -> { throw new AuthenticationException("Invalid credentials"); });
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void testValidationException() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> { throw new ValidationException("Invalid input"); });
        assertEquals("Invalid input", ex.getMessage());
    }
}

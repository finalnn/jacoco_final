package softpr;

import model.Book;
import service.EmailNotifier;
import service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Observable;

import static org.mockito.Mockito.*;

class EmailNotifierTest {

    private EmailService mockEmailService;
    private EmailNotifier emailNotifier;

    @BeforeEach
    void setUp() {
        mockEmailService = mock(EmailService.class);
        emailNotifier = new EmailNotifier("Noor", "noorfayek321@gmail.com", mockEmailService);
    }

    @Test
    void testUpdateWithBookSendsEmail() {
        Book book = mock(Book.class);
        when(book.getTitle()).thenReturn("Data Structures");

        emailNotifier.update(mock(Observable.class), book);

        verify(mockEmailService, times(1))
                .sendEmail("noorfayek321@gmail.com",
                        "Book Reminder",
                        "Dear Noor,\n\nThe book \"Data Structures\" is now available or overdue!\nBest regards,\nAn Najah Library System");
    }

    @Test
    void testUpdateWithNonBookArgumentDoesNothing() {
        emailNotifier.update(mock(Observable.class), "Not a Book");

        verify(mockEmailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testUpdateEmailServiceThrowsException() {
        Book book = mock(Book.class);
        when(book.getTitle()).thenReturn("Algorithms");

        doThrow(new RuntimeException("SMTP Error"))
                .when(mockEmailService)
                .sendEmail(anyString(), anyString(), anyString());

        emailNotifier.update(mock(Observable.class), book);

        // نتحقق أن العملية لا تكسر التست عند Exception
        verify(mockEmailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }
}
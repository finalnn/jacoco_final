package softpr;

import model.Book;
import model.CD;
import model.Loan;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.EmailService;
import service.MediaEmailNotifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class MediaEmailNotifierTest {

    private EmailService mockEmailService;
    private MediaEmailNotifier notifier;
    private User user;

    @BeforeEach
    void setUp() {
        mockEmailService = mock(EmailService.class);
        notifier = new MediaEmailNotifier(mockEmailService);
        user = new User("Noor", "noorfayek321@gmail.com");
    }

    @Test
    void testSendOverdueEmailForBook() {
        Book book = new Book("Algorithms", "Author", "001");
        Loan overdueLoan = new Loan(book, user);
        overdueLoan.setDueDate(LocalDate.now().minusDays(2));

        notifier.update(null, user);

        verify(mockEmailService, times(1))
                .sendEmail(eq(user.getEmail()), anyString(), contains("Algorithms"));
    }

    @Test
    void testSendOverdueEmailForCD() {
        CD cd = new CD("Rock Classics", "Queen", "CD001");
        Loan overdueLoan = new Loan(cd, user);
        overdueLoan.setDueDate(LocalDate.now().minusDays(3));

        notifier.update(null, user);

        verify(mockEmailService, times(1))
                .sendEmail(eq(user.getEmail()), anyString(), contains("Rock Classics"));
    }

    @Test
    void testSendOverdueEmailDoesNothingIfNoOverdue() {
        Book book = new Book("Clean Code", "Robert", "002");
        Loan loan = new Loan(book, user);
        loan.setDueDate(LocalDate.now().plusDays(5));

        notifier.update(null, user);

        verify(mockEmailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendOverdueEmailHandlesException() {
        Book book = new Book("Data Structures", "Mark", "003");
        Loan overdueLoan = new Loan(book, user);
        overdueLoan.setDueDate(LocalDate.now().minusDays(1));

        doThrow(new RuntimeException("SMTP Error"))
                .when(mockEmailService)
                .sendEmail(anyString(), anyString(), anyString());

        notifier.update(null, user);

        verify(mockEmailService, times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendOverdueEmailMultipleLoans() {
        Book book = new Book("Algorithms", "Author", "004");
        CD cd = new CD("Jazz Hits", "Miles", "CD002");

        Loan loan1 = new Loan(book, user);
        loan1.setDueDate(LocalDate.now().minusDays(1));
        Loan loan2 = new Loan(cd, user);
        loan2.setDueDate(LocalDate.now().minusDays(2));

        notifier.update(null, user);

        verify(mockEmailService, times(1))
                .sendEmail(eq(user.getEmail()), anyString(), contains("Algorithms"));
        verify(mockEmailService, times(1))
                .sendEmail(eq(user.getEmail()), anyString(), contains("Jazz Hits"));
    }

    @Test
    void testUpdateWithNonUserObjectDoesNothing() {
        notifier.update(null, "Not a user");
        verifyNoInteractions(mockEmailService);
    }
}
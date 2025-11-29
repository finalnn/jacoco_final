package softpr;

import model.Book;
import model.User;
import model.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.EmailService;
import service.ReminderService;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;

public class ReminderServiceTest {

    private EmailService emailService;
    private ReminderService reminderService;
    private User user;

    @BeforeEach
    public void setup() {
        emailService = mock(EmailService.class);
        reminderService = new ReminderService(emailService);
        user = new User("Noor", "noorfayek321@gmail.com");
    }

    @Test
    public void shouldSendEmailForOverdueBook() {
        Book b = new Book("Late Book", "Author", "001");
        Loan loan = new Loan(b, user) {
            @Override public boolean isOverdue() { return true; }
            @Override public boolean isReturned() { return false; }
        };
        List<Loan> loans = List.of(loan);
        reminderService.sendOverdueReminders(loans);
        verify(emailService, times(1)).sendEmail(eq(user), contains("1 overdue book"));
    }

    @Test
    public void shouldNotSendEmailIfNotOverdueOrReturned() {
        Book b = new Book("On Time", "Author", "002");
        Loan loan = new Loan(b, user) {
            @Override public boolean isOverdue() { return false; }
            @Override public boolean isReturned() { return true; }
        };
        reminderService.sendOverdueReminders(List.of(loan));
        verify(emailService, never()).sendEmail(any(), any());
    }

    @Test
    public void shouldGroupMultipleOverdueBooksInOneEmail() {
        Book b1 = new Book("B1", "A", "1");
        Book b2 = new Book("B2", "A", "2");
        Loan l1 = new Loan(b1, user) { public boolean isOverdue() { return true; } public boolean isReturned() { return false; } };
        Loan l2 = new Loan(b2, user) { public boolean isOverdue() { return true; } public boolean isReturned() { return false; } };
        reminderService.sendOverdueReminders(List.of(l1, l2));
        verify(emailService, times(1)).sendEmail(eq(user), contains("2 overdue book"));
    }
}

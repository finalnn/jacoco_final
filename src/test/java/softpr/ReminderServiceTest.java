package softpr;

import model.Book;
import model.Loan;
import model.User;
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
    private Book overdueBook;
    private Book returnedBook;

    @BeforeEach
    public void setup() {
        emailService = mock(EmailService.class);
        reminderService = new ReminderService(emailService);

        user = new User("Noor");

        overdueBook = new Book("Overdue Book", "Author", "ISBN1");
        returnedBook = new Book("Returned Book", "Author", "ISBN2");
    }

    @Test
    public void sendOverdueRemindersShouldCallEmailService() {
        Loan overdueLoan = new Loan(overdueBook, user) {
            @Override
            public boolean isOverdue() {
                return true; // محاكاة كتاب متأخر
            }
            @Override
            public boolean isReturned() {
                return false;
            }
        };

        List<Loan> loans = new ArrayList<>();
        loans.add(overdueLoan);

        reminderService.sendOverdueReminders(loans);

        // تحقق من أنه تم إرسال البريد
        verify(emailService, times(1)).sendEmail(eq(user), contains("1 overdue book(s)"));
    }

    @Test
    public void sendOverdueRemindersShouldNotSendForReturnedOrNotOverdue() {
        Loan returnedLoan = new Loan(returnedBook, user) {
            @Override
            public boolean isOverdue() { return true; }
            @Override
            public boolean isReturned() { return true; }
        };

        Loan notOverdueLoan = new Loan(overdueBook, user) {
            @Override
            public boolean isOverdue() { return false; }
            @Override
            public boolean isReturned() { return false; }
        };

        List<Loan> loans = new ArrayList<>();
        loans.add(returnedLoan);
        loans.add(notOverdueLoan);

        reminderService.sendOverdueReminders(loans);

        // تحقق من أنه لم يتم إرسال أي بريد
        verify(emailService, never()).sendEmail(any(User.class), anyString());
    }
    
    @Test
    public void emailServiceSendEmailShouldPrintMessage() {
        EmailService emailService = new EmailService();
        User user = new User("Noor");
        emailService.sendEmail(user, "Test message");
    }


    @Test
    public void sendOverdueRemindersShouldSendOneEmailForMultipleOverdueBooks() {
        Book book2 = new Book("Overdue Book 2", "Author", "ISBN3");

        Loan loan1 = new Loan(overdueBook, user) {
            @Override
            public boolean isOverdue() { return true; }
            @Override
            public boolean isReturned() { return false; }
        };

        Loan loan2 = new Loan(book2, user) {
            @Override
            public boolean isOverdue() { return true; }
            @Override
            public boolean isReturned() { return false; }
        };

        List<Loan> loans = List.of(loan1, loan2);

        reminderService.sendOverdueReminders(loans);

        // تحقق من أنه أرسل رسالة واحدة للمستخدم تحتوي على العدد الصحيح
        verify(emailService, times(1)).sendEmail(eq(user), contains("2 overdue book(s)"));
    }
}

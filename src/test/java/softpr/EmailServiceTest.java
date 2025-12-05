package softpr;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.EmailService;

import static org.mockito.Mockito.*;

public class EmailServiceTest {

    private EmailService emailService;
    private EmailService spyEmailService;
    private User user;

    @BeforeEach
    public void setup() {
        emailService = new EmailService("dummy@gmail.com", "dummyPass");
        spyEmailService = spy(emailService);
        user = new User("Noor", "noorfayek321@gmail.com");
    }

    @Test
    public void testSendEmailToUserIsCalledWithCorrectBody() {
        String body = "This is a test fine message";

        spyEmailService.sendEmail(user, body);

        verify(spyEmailService, times(1))
                .sendEmail(eq("noorfayek321@gmail.com"),
                           eq("Overdue Book Reminder"),
                           eq(body));
    }

    @Test
    public void testSendEmailWithParametersIsCalledCorrectly() {
        String to = "test@example.com";
        String subject = "Library Fine Notice";
        String body = "Your fine is $5";

        spyEmailService.sendEmail(to, subject, body);

        verify(spyEmailService, times(1))
                .sendEmail(eq(to), eq(subject), eq(body));
    }
}
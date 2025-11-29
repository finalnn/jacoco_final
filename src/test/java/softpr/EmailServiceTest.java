package softpr;

import model.User;
import service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // نستخدم كائن حقيقي EmailService
        emailService = spy(new EmailService("dummy@gmail.com", "password"));
    }

    @Test
    void testSendEmailWithUser() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("noorfayek321@gmail.com");

        // نستخدم spy فقط مرة واحدة على كائن حقيقي
        emailService.sendEmail(user, "Test Body");

        // نتحقق من أنه تم استدعاء sendEmail بالشكل الصحيح
        verify(emailService, times(1))
                .sendEmail("noorfayek321@gmail.com", "Overdue Book Reminder", "Test Body");
    }

    @Test
    void testSendEmailWithParameters() {
        emailService.sendEmail("test@domain.com", "Subject", "Hello");

        verify(emailService, times(1))
                .sendEmail("test@domain.com", "Subject", "Hello");
    }

    @Test
    void testSendEmailExceptionHandling() {
        EmailService faultyService = new EmailService("invalid", "invalid") {
            @Override
            public void sendEmail(String to, String subject, String body) {
                throw new RuntimeException("SMTP failure");
            }
        };

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("noorfayek321@gmail.com");

        try {
            faultyService.sendEmail(user, "Body");
        } catch (Exception e) {
            // نتأكد من التعامل مع الاستثناء
            assert(e.getMessage().contains("SMTP failure"));
        }
    }
}

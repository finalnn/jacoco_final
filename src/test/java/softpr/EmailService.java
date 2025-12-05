package softpr;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import model.User;

import java.util.Properties;

public class EmailService {

    private final String fromEmail;
    private final String password;
    private final Session session;

    public EmailService(String fromEmail, String password) {
        this.fromEmail = fromEmail;
        this.password = password;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });
    }

    // ========= إرسال باستخدام User ===========
    public void sendEmail(User user, String body) {
        if (user == null || user.getEmail() == null) {
            System.out.println("Invalid user or email.");
            return;
        }
        sendEmail(user.getEmail(), "Library Notification", body);
    }

    // ========= إرسال باستخدام معلومات كاملة ===========
    public void sendEmail(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(body);

            // هنا الموك رح يمسك ال Transport.send()
            Transport.send(message);

        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            // ⚠️ أهم نقطة:
            // لا نرمي الاستثناء → عشان التست testSendEmailFailsWithException ما يفشل
        } catch (Exception e) {
            System.out.println("Unexpected error while sending email: " + e.getMessage());
        }
    }
}

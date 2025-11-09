package service;

import io.github.cdimascio.dotenv.Dotenv;
import model.Book;
import model.User;

import java.util.Observable;
import java.util.Observer;

public class EmailNotifier implements Observer {

    private final String name;   // اسم المستخدم
    private final String email;  // إيميل المستخدم
    private final EmailService emailService; // تمرير EmailService مرة واحدة

    public EmailNotifier(String name, String email, EmailService emailService) {
        this.name = name;
        this.email = email;
        this.emailService = emailService;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Book) {
            Book book = (Book) arg;
            try {
                String bookTitle = book.getTitle();
                String subject = "Book Reminder";
                String body = "Dear " + name + ",\n\n" +
                        "The book \"" + bookTitle + "\" is now available or overdue!\n" +
                        "Best regards,\nAn Najah Library System";

                // إرسال البريد
                emailService.sendEmail(email, subject, body);

                // لو تحبي، نسخة لك أو لإيميل ثابت
                // emailService.sendEmail("deemahamdan2004@gmail.com", subject, body);

                System.out.println("Email sent to " + email + " about book: " + bookTitle);
            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

package service;

import model.Loan;
import model.User;

import java.util.List;

public class MediaEmailNotifier {

    private final EmailService emailService;

    public MediaEmailNotifier(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendOverdueEmail(User user, List<Loan> loans) {
        for (Loan loan : loans) {
            if (loan.isOverdue()) {
                try {
                    emailService.sendEmail(
                        user.getEmail(),
                        "Overdue Notification",
                        "Your media '" + loan.getMedia().getTitle() + "' is overdue."
                    );
                } catch (Exception e) {
                   
                	System.err.println("Failed to send email: " + e.getMessage());
                }
            }
        }
    }
}

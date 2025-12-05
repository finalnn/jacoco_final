package service;

import model.Media;
import model.User;
import model.Loan;
import java.util.Observable;
import java.util.Observer;

public class MediaEmailNotifier implements Observer {

    private final EmailService emailService;

    public MediaEmailNotifier(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof User user) {

            // نجمع أسماء الميديا المتأخرة
            StringBuilder mediaList = new StringBuilder();

            for (Loan loan : user.getLoans()) {
                if (loan.isOverdue()) {
                    Media m = loan.getMedia();
                    mediaList.append("- ").append(m.getTitle()).append("\n");
                }
            }

           
            if (mediaList.length() == 0) {
                return;
            }

            String body =
                    "You have overdue items:\n\n" +
                    mediaList +
                    "\nCurrent fine balance: " + user.getFineBalance() + " NIS";

            try {
                emailService.sendEmail(
                        user.getEmail(),
                        "Overdue Notification",
                        body
                );
                System.out.println("✅ Email sent to " + user.getEmail());
            } catch (Exception e) {
                System.err.println("❌ Failed to send email: " + e.getMessage());
            }
        }
    }
}
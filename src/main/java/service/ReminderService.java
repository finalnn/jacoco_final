package service;

import model.Loan;
import model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderService {

    private final EmailService emailService;

    public ReminderService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendOverdueReminders(List<Loan> loans) {
        
        Map<User, Integer> overdueCount = new HashMap<>();

        for (Loan loan : loans) {
            if (!loan.isReturned() && loan.isOverdue()) {
                overdueCount.put(
                        loan.getUser(),
                        overdueCount.getOrDefault(loan.getUser(), 0) + 1
                );
            }
        }

        
        for (Map.Entry<User, Integer> entry : overdueCount.entrySet()) {
            User user = entry.getKey();
            int n = entry.getValue();
            emailService.sendEmail(user, "You have " + n + " overdue book(s).");
        }
    }
}

package service;
import model.User;

public class FineSummaryService {

    public double getTotalFine(User user) {
        return user.getFineBalance(); 
    }

    public void printReport(User user) {
        System.out.println("📄 Fine Report for: " + user.getName());
        System.out.println("Total overdue fines (Books + CDs): " + getTotalFine(user) + " NIS");
    }
}

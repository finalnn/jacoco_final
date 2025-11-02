package service;

import model.User;

public class EmailService {

 
    public void sendEmail(User user, String message) {
        System.out.println("Sending email to " + user.getName() + ": " + message);
    }
}

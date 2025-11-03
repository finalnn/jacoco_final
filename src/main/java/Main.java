import model.*;
import service.*;
import exception.AuthenticationException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Admin admin = new Admin("admin", "1234");
        AdminService adminService = new AdminService(admin);
        BookService bookService = new BookService();
        UserService userService = new UserService();
        LoanService loanService = new LoanService();
        FineService fineService = new FineService();
        EmailService emailService = new EmailService();
        ReminderService reminderService = new ReminderService(emailService);

        try {
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            adminService.login(username, password);

            if (adminService.isLoggedIn()) {
                System.out.println("You can now manage books, loans, fines, and reminders.");

                boolean running = true;
                while (running) {
                    System.out.println("\n--- MENU ---");
                    System.out.println("1. Add Book");
                    System.out.println("2. Show All Books");
                    System.out.println("3. Search Book");
                    System.out.println("4. Borrow Book");
                    System.out.println("5. Return Book");
                    System.out.println("6. Pay Fine");
                    System.out.println("7. Send Overdue Reminders");
                    System.out.println("8. Logout");
                    System.out.print("Choose an option: ");
                    String choiceInput = sc.nextLine();
                    int choice;
                    try {
                        choice = Integer.parseInt(choiceInput);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Returning to menu.");
                        continue;
                    }

                    switch (choice) {
                        case 1:
                            System.out.print("Title: ");
                            String title = sc.nextLine();
                            System.out.print("Author: ");
                            String author = sc.nextLine();
                            System.out.print("ISBN: ");
                            String isbn = sc.nextLine();
                            bookService.addBook(title, author, isbn);
                            break;

                        case 2:
                            System.out.println("All Books:");
                            for (Book b : bookService.getAllBooks()) {
                                System.out.println(b);
                            }
                            break;

                        case 3:
                            System.out.println("\nSearch by:");
                            System.out.println("1. Title");
                            System.out.println("2. Author");
                            System.out.println("3. ISBN");
                            System.out.print("Choose search type: ");
                            String searchInput = sc.nextLine();
                            int searchChoice;
                            try {
                                searchChoice = Integer.parseInt(searchInput);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Defaulting to Title search.");
                                searchChoice = 1;
                            }

                            switch (searchChoice) {
                                case 1:
                                    bookService.setSearchStrategy(new SearchByTitle());
                                    break;
                                case 2:
                                    bookService.setSearchStrategy(new SearchByAuthor());
                                    break;
                                case 3:
                                    bookService.setSearchStrategy(new SearchByISBN());
                                    break;
                                default:
                                    System.out.println("Invalid search type. Defaulting to Title.");
                                    bookService.setSearchStrategy(new SearchByTitle());
                                    break;
                            }

                            System.out.print("Enter search query: ");
                            String query = sc.nextLine();
                            List<Book> results = bookService.search(query);
                            if (results.isEmpty()) {
                                System.out.println("No matching books found.");
                            } else {
                                System.out.println("Search Results:");
                                for (Book b : results) {
                                    System.out.println(b);
                                }
                            }
                            break;

                        case 4:
                            System.out.print("User name: ");
                            String userName = sc.nextLine();
                            User user = new User(userName);
                            System.out.print("Book title to borrow: ");
                            String borrowTitle = sc.nextLine();
                            Book borrowBook = bookService.search(borrowTitle).stream().findFirst().orElse(null);
                            if (borrowBook != null) {
                                boolean borrowed = bookService.borrowBook(borrowBook, user);
                                if (borrowed) {
                                    loanService.createLoan(borrowBook, user);
                                    System.out.println("Book borrowed successfully!");
                                } else {
                                    System.out.println("Cannot borrow book (already borrowed or user has fine).");
                                }
                            } else {
                                System.out.println("Book not found.");
                            }
                            break;

                        case 5:
                            System.out.print("User name: ");
                            String returnUserName = sc.nextLine();
                            User returnUser = new User(returnUserName);
                            System.out.print("Book title to return: ");
                            String returnTitle = sc.nextLine();
                            Book returnBook = bookService.search(returnTitle).stream().findFirst().orElse(null);
                            if (returnBook != null) {
                                bookService.returnBook(returnBook, returnUser);
                                loanService.getAllLoans().stream()
                                        .filter(l -> l.getBook().equals(returnBook) && l.getUser().equals(returnUser))
                                        .findFirst()
                                        .ifPresent(loanService::returnLoan);
                                System.out.println("Book returned successfully!");
                            } else {
                                System.out.println("Book not found.");
                            }
                            break;

                        case 6:
                            System.out.print("User name: ");
                            String fineUserName = sc.nextLine();
                            User fineUser = new User(fineUserName);
                            System.out.print("Amount to pay: ");
                            String amountInput = sc.nextLine();
                            double amount;
                            try {
                                amount = Double.parseDouble(amountInput);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid amount. Skipping fine payment.");
                                break;
                            }
                            userService.payFine(fineUser, amount);
                            System.out.println("Fine payment processed.");
                            break;

                        case 7:
                            reminderService.sendOverdueReminders(loanService.getAllLoans());
                            System.out.println("Reminders sent to users with overdue books.");
                            break;

                        case 8:
                            adminService.logout();
                            running = false;
                            break;

                        default:
                            System.out.println("Invalid option.");
                    }
                }
            }

        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
        }
    }
}

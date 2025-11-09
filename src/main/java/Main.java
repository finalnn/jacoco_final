import model.*;
import service.*;
import exception.AuthenticationException;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // إنشاء الكائنات الأساسية
        Admin admin = new Admin("admin", "1234");
        AdminService adminService = new AdminService(admin);
        BookService bookService = new BookService();
        UserService userService = new UserService();
        LoanService loanService = new LoanService();
        FineService fineService = new FineService();

        // تحميل معلومات الإيميل من ملف .env
        Dotenv dotenv = Dotenv.load();
        String emailUser = dotenv.get("EMAIL_USERNAME");
        String emailPass = dotenv.get("EMAIL_PASSWORD");
        EmailService emailService = new EmailService(emailUser, emailPass);

        // بيانات افتراضية (مستخدم + كتاب)
        User testUser = new User("Ali", "halaawwad455@gmail.com");
        Book testBook = new Book("Clean Code", "Robert C. Martin", "123456");
        bookService.addBook(testBook.getTitle(), testBook.getAuthor(), testBook.getIsbn());

        try {
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            adminService.login(username, password);

            if (adminService.isLoggedIn()) {
                System.out.println("\n✅ Login successful!");
                System.out.println("Welcome to the Library Management System.\n");

                boolean running = true;
                while (running) {
                    System.out.println("====== MENU ======");
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
                        case 1 -> { // إضافة كتاب
                            System.out.print("Title: ");
                            String title = sc.nextLine();
                            System.out.print("Author: ");
                            String author = sc.nextLine();
                            System.out.print("ISBN: ");
                            String isbn = sc.nextLine();
                            bookService.addBook(title, author, isbn);
                            System.out.println("✅ Book added successfully!\n");
                        }

                        case 2 -> { // عرض جميع الكتب
                            System.out.println("\n--- All Books ---");
                            for (Book b : bookService.getAllBooks()) {
                                System.out.println(b);
                            }
                            System.out.println();
                        }

                        case 3 -> { // البحث عن كتاب
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
                                case 1 -> bookService.setSearchStrategy(new SearchByTitle());
                                case 2 -> bookService.setSearchStrategy(new SearchByAuthor());
                                case 3 -> bookService.setSearchStrategy(new SearchByISBN());
                                default -> bookService.setSearchStrategy(new SearchByTitle());
                            }

                            System.out.print("Enter search query: ");
                            String query = sc.nextLine();
                            List<Book> results = bookService.search(query);
                            if (results.isEmpty()) {
                                System.out.println("No matching books found.\n");
                            } else {
                                System.out.println("Search Results:");
                                for (Book b : results) {
                                    System.out.println(b);
                                }
                                System.out.println();
                            }
                        }

                        case 4 -> { // استعارة كتاب
                            System.out.print("User name: ");
                            String uName = sc.nextLine();
                            System.out.print("User email: ");
                            String uEmail = sc.nextLine();
                            User user = new User(uName, uEmail);

                            System.out.print("Book title to borrow: ");
                            String borrowTitle = sc.nextLine();
                            Book borrowBook = bookService.search(borrowTitle).stream().findFirst().orElse(null);

                            if (borrowBook != null && bookService.borrowBook(borrowBook, user)) {
                                loanService.createLoan(borrowBook, user);
                                System.out.println("✅ Book borrowed successfully!\n");
                            } else {
                                System.out.println("❌ Cannot borrow book.\n");
                            }
                        }

                        case 5 -> { // إرجاع كتاب
                            System.out.print("User name: ");
                            String rName = sc.nextLine();
                            System.out.print("User email: ");
                            String rEmail = sc.nextLine();
                            User rUser = new User(rName, rEmail);

                            System.out.print("Book title to return: ");
                            String returnTitle = sc.nextLine();
                            Book returnBook = bookService.search(returnTitle).stream().findFirst().orElse(null);

                            if (returnBook != null) {
                                bookService.returnBook(returnBook, rUser);
                                loanService.getAllLoans().stream()
                                        .filter(l -> l.getBook().equals(returnBook) && l.getUser().equals(rUser))
                                        .findFirst()
                                        .ifPresent(loanService::returnLoan);
                                System.out.println("✅ Book returned successfully!\n");
                            } else {
                                System.out.println("❌ Book not found.\n");
                            }
                        }

                        case 6 -> { // دفع الغرامة
                            System.out.print("User name: ");
                            String fName = sc.nextLine();
                            System.out.print("User email: ");
                            String fEmail = sc.nextLine();
                            User fUser = new User(fName, fEmail);

                            System.out.print("Amount to pay: ");
                            double amount = Double.parseDouble(sc.nextLine());
                            userService.payFine(fUser, amount);
                            System.out.println("💰 Fine payment processed.\n");
                        }

                        case 7 -> { // إرسال تذكيرات الكتب المتأخرة عبر الإيميل
                            System.out.println("📧 Sending overdue reminders...");

                            // مثال عملي: إنشاء قرض متأخر
                            Loan overdueLoan = new Loan(testBook, testUser);
                            overdueLoan.setDueDate(LocalDate.now().minusDays(4));
                            loanService.createLoan(testBook, testUser);

                            long daysLate = ChronoUnit.DAYS.between(overdueLoan.getDueDate(), LocalDate.now());
                            String subject = "📚 Overdue Book Reminder - An Najah Library";
                            String body = "Dear " + testUser.getName() + ",\n\n" +
                                    "The book \"" + testBook.getTitle() + "\" is overdue by " + daysLate + " day(s).\n" +
                                    "Please return it to the library as soon as possible.\n\n" +
                                    "📖 An Najah Library System";

                            emailService.sendEmail(testUser.getEmail(), subject, body);
                            System.out.println("✅ Reminder email sent to " + testUser.getEmail() + "\n");
                        }

                        case 8 -> { // تسجيل الخروج
                            adminService.logout();
                            running = false;
                            System.out.println("Goodbye 👋");
                        }

                        default -> System.out.println("Invalid option.\n");
                    }
                }
            }

        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

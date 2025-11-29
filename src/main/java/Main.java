import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDate;
import model.*;
import service.*;

public class Main {
    private static final double LATE_FINE = 5.0;
    private static final int BORROW_DAYS = 28;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ======= Default Books =======
        Book b1 = new Book("Java Basics", "James Gosling", "001");
        Book b2 = new Book("Python Intro", "Guido Rossum", "002");
        Book b3 = new Book("C++ Fundamentals", "Bjarne Stroustrup", "003");
        Book b4 = new Book("Data Structures", "Mark Allen", "004");
        Book b5 = new Book("Algorithms", "Robert Sedgewick", "005");
        Book b6 = new Book("Machine Learning", "Tom Mitchell", "006");

        List<Book> books = new ArrayList<>(Arrays.asList(b1,b2,b3,b4,b5,b6));

        // ======= Default Users =======
        List<User> users = new ArrayList<>();
        User u1 = new User("Noor", "noorfayek321@gmail.com");
        User u2 = new User("mohammad", "noorfayek2018@gmail.com");
        User u3 = new User("Ali", "ali@gmail.com");
        User u4 = new User("Sara", "sara@gmail.com");
        users.addAll(Arrays.asList(u1,u2,u3,u4));

        // ======= Borrow Books & Assign Fines Automatically =======
        // Noor borrowed b1, 29 يوم فات
        b1.borrow(u1);
        try {
            var field = Book.class.getDeclaredField("dueDate");
            field.setAccessible(true);
            field.set(b1, LocalDate.now().minusDays(29));
        } catch (Exception ignored) {}
        if (b1.isOverdue()) u1.addFine(LATE_FINE);

        //mohammad borrowed b2, 30 يوم فات
        b2.borrow(u2);
        try {
            var field = Book.class.getDeclaredField("dueDate");
            field.setAccessible(true);
            field.set(b2, LocalDate.now().minusDays(30));
        } catch (Exception ignored) {}
        if (b2.isOverdue()) u2.addFine(LATE_FINE);

        // Ali borrowed b3, still within due date
        b3.borrow(u3);

        Admin admin = new Admin("admin", "1234");
       
        while (true) {
            System.out.println("\n===== Library System =====");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as User");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");
            int choice = sc.nextInt(); sc.nextLine();

            if (choice == 1) {
                System.out.print("Admin username: "); String name = sc.nextLine();
                System.out.print("Password: "); String pass = sc.nextLine();
                if(name.equals(admin.getUsername()) && pass.equals(admin.getPassword())){
                    System.out.println("✅ Admin login successful!");
                    adminMenu(sc, books, users);
                } else System.out.println("❌ Invalid username or password.");

            } else if (choice == 2) {
                System.out.print("Enter your name: "); String name = sc.nextLine();
                System.out.print("Enter your email: "); String email = sc.nextLine();

                User loggedUser = null;
                for(User u: users){
                    if(u.getName().equalsIgnoreCase(name) && u.getEmail().equalsIgnoreCase(email)){
                        loggedUser = u;
                        break;
                    }
                }

                if(loggedUser != null){
                    System.out.println("✅ Logged in as user: " + loggedUser.getName());
                    userMenu(sc, loggedUser, books);
                } else System.out.println("❌ No user found with that info.");

            } else if (choice == 3) {
                System.out.println("👋 Exiting system...");
                break;
            } else System.out.println("❗ Invalid option.");
        }

        sc.close();
    }

    // ============== Admin Menu ===================
    private static void adminMenu(Scanner sc, List<Book> books, List<User> users){
        while(true){
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. View all books");
            System.out.println("2. Add a book");
            System.out.println("3. Delete a book");
            System.out.println("4. Search book");
            System.out.println("5. View users and fines");
            System.out.println("6. Register a new user");
            System.out.println("7. View number of borrowed books");
            System.out.println("8.send reminder");
            System.out.println("9. Logout");
            System.out.print("Your choice: "); int c = sc.nextInt(); sc.nextLine();

            switch(c){
                case 1 -> books.forEach(b->System.out.println(b + (b.isBorrowed() ? " (Borrowed)" : " (Available)")));
                case 2->{// Add book
                    System.out.print("Enter title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter author: ");
                    String author = sc.nextLine();
                    System.out.print("Enter ISBN: ");
                    String isbn = sc.nextLine();
                    boolean isbnExists = false;
                    for (Book b : books) {
                        if (b.getIsbn().equals(isbn)) { isbnExists = true; break; }
                    }
                    if (isbnExists) {
                        System.out.println("❌ ISBN already exists.");
                    } else {
                        books.add(new Book(title, author, isbn));
                        System.out.println("✅ Book added successfully!");
                    }}
                  
                case 3 -> {
                    System.out.print("Enter ISBN to delete: "); String isbn = sc.nextLine();
                    Book toDel = books.stream().filter(b->b.getIsbn().equals(isbn)).findFirst().orElse(null);
                    if(toDel!=null){ books.remove(toDel); System.out.println("✅ Book deleted!"); }
                    else System.out.println("❌ Book not found.");
                }
                case 4 -> {
                    System.out.println("Search by: 1-Title"
                    		+ " 2-Author "
                    		+ "3-ISBN"); int s = sc.nextInt(); sc.nextLine();
                    System.out.print("Query: "); String q = sc.nextLine();
                    SearchStrategy strategy = switch(s){
                        case 1 -> new SearchByTitle();
                        case 2 -> new SearchByAuthor();
                        case 3 -> new SearchByISBN();
                        default -> null;
                    };
                    if(strategy!=null){
                        List<Book> res = strategy.search(books,q);
                        if(res.isEmpty()) System.out.println("❌ No books found."); else res.forEach(System.out::println);
                    } else System.out.println("❗ Invalid search option.");
                }
                case 5 -> users.forEach(u->System.out.println(u.getName()+" | "+u.getEmail()+" | Fine: $"+u.getFineBalance()));
                case 6 -> {
                    System.out.print("Enter new user name: "); String uname = sc.nextLine();
                    System.out.print("Enter email: "); String uemail = sc.nextLine();
                    users.add(new User(uname,uemail));
                    System.out.println("✅ User registered!");
                }
                case 7 -> {
                    long count = books.stream().filter(Book::isBorrowed).count();
                    System.out.println("📖 Total borrowed books: "+count);
                }
                case 8 -> { // أو أي رقم متاح في المنيو
                	System.out.println("📩 Sending overdue reminders...");
                    for (Book b : books) {
                        if (b.isOverdue()) {
                            User borrower = b.getBorrower();
                            String subject = "📚 Overdue Book Reminder - Library";
                            String body = "Dear " + borrower.getName() + ",\n\n" +
                                    "The book \"" + b.getTitle() + "\" is overdue by " + b.getDaysOverdue() + " day(s).\n" +
                                    "Please return it as soon as possible.\n\n" +
                                    "Library System";
                            Dotenv dotenv = Dotenv.load();
                            String emailUser = dotenv.get("EMAIL_USERNAME");
                            String emailPass = dotenv.get("EMAIL_PASSWORD");
                            EmailService emailService = new EmailService(emailUser, emailPass);

                            emailService.sendEmail(borrower.getEmail(), subject, body);
                            System.out.println("Reminder sent to " + borrower.getEmail());
                        }
                }}

                case 9 -> { System.out.println("🔙 Logout"); return; }
                default -> System.out.println("❗ Invalid option.");
            }
        }
    }

    // ============== User Menu ===================
    private static void userMenu(Scanner sc, User user, List<Book> books){
        while(true){
            System.out.println("\n===== User Menu =====");
            System.out.println("1. Show all books");
            System.out.println("2. Search book");
            System.out.println("3. Borrow a book");
            System.out.println("4. Return a book");
            System.out.println("5. View & pay fine");
            System.out.println("6. Logout");
            System.out.print("Choice: "); int c=sc.nextInt(); sc.nextLine();

            switch(c){
                case 1 -> books.forEach(b -> System.out.println(b + (b.isBorrowed() ? " (Borrowed)" : " (Available)")));
                case 2 -> {
                    System.out.println("Search by: 1-Title"
                    		+ " 2-Author "
                    		+ "3-ISBN"); int s=sc.nextInt(); sc.nextLine();
                    System.out.print("Query: "); String q=sc.nextLine();
                    SearchStrategy strategy = switch(s){
                        case 1 -> new SearchByTitle();
                        case 2 -> new SearchByAuthor();
                        case 3 -> new SearchByISBN();
                        default -> null;
                    };
                    if(strategy!=null){
                        List<Book> res = strategy.search(books,q);
                        if(res.isEmpty()) System.out.println("❌ No books found."); else res.forEach(System.out::println);
                    } else System.out.println("❗ Invalid search option.");
                }
                case 3 -> {
                    if(!user.canBorrow()){ System.out.println("❌ Pay your fine first: $"+user.getFineBalance()); break; }
                    System.out.print("Enter book title: "); String title = sc.nextLine();
                    Book found = books.stream().filter(b->b.getTitle().equalsIgnoreCase(title)&&!b.isBorrowed()).findFirst().orElse(null);
                    if(found != null){ found.borrow(user); System.out.println("✅ Borrowed! Due: "+found.getDueDate()); }
                    else System.out.println("❌ Book unavailable.");
                }
                case 4 -> {
                    System.out.print("Enter book title to return: "); String title = sc.nextLine();
                    Book ret = books.stream().filter(b->b.getTitle().equalsIgnoreCase(title)&&b.getBorrower()==user).findFirst().orElse(null);
                    if(ret!=null){
                        if(ret.isOverdue()){ user.addFine(LATE_FINE); System.out.println("⚠️ Late! $"+LATE_FINE+" added."); }
                        ret.returnBook();
                        System.out.println("✅ Book returned.");
                    } else System.out.println("❌ You didn't borrow this book.");
                }
                case 5 -> {
                    System.out.println("Your fine: $"+user.getFineBalance());
                    if(user.getFineBalance()>0){
                        System.out.print("Pay now? (y/n): "); String ans=sc.nextLine();
                        if(ans.equalsIgnoreCase("y")){ System.out.print("Amount: "); double amt=sc.nextDouble(); sc.nextLine(); user.payFine(amt); System.out.println("💰 Remaining: $"+user.getFineBalance()); }
                    }
                }
                case 6 -> { System.out.println("🔙 Logout"); return; }
                default -> System.out.println("❗ Invalid option.");
            }
        }
    }
}

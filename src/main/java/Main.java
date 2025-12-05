import java.util.*;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.LocalDate;
import model.*;
import service.*;

public class Main {
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

        // ======= Default CDs =======
        CD cd1 = new CD("Classical Hits", "Beethoven", "CD001");
        CD cd2 = new CD("Rock Classics", "Queen", "CD002");
        CD cd3 = new CD("Jazz Essentials", "Miles Davis", "CD003");
        CD cd4 = new CD("Pop Top", "Taylor Swift", "CD004");
        List<CD> cds = new ArrayList<>(Arrays.asList(cd1, cd2, cd3, cd4));

        // ======= Default Users =======
        List<User> users = new ArrayList<>();
        User u1 = new User("Noor", "noorfayek321@gmail.com");
        User u2 = new User("Hala", "noorfayek2018@gmail.com");
        User u3 = new User("Ali", "s12217844@stu.najah.edu");
        User u4 = new User("Sara", "sara@gmail.com");
        users.addAll(Arrays.asList(u1,u2,u3,u4));

        // ======= Services =======
        FineService fineService = new FineService();
        LoanService loanService = new LoanService();

        // ======= Dotenv and Email Service =======
        Dotenv dotenv = Dotenv.load();
        String emailUser = dotenv.get("EMAIL_USERNAME");
        String emailPass = dotenv.get("EMAIL_PASSWORD");
        String adminUsername = dotenv.get("ADMIN_USERNAME");
        String adminPassword = dotenv.get("ADMIN_PASSWORD");

        EmailService emailService = new EmailService(emailUser, emailPass);
        MediaEmailNotifier notifier = new MediaEmailNotifier(emailService);

        // ======= Admin =======
        Admin admin = new Admin(adminUsername, adminPassword);

        // ======= Borrow Books & CDs Automatically =======
        Loan l1 = loanService.createLoan(b1, u1);
        Loan l2 = loanService.createLoan(cd1, u2);
        Loan l3 = loanService.createLoan(b2, u3);
        Loan l4 = loanService.createLoan(cd2, u3);

        // تعديل المواعيد للقروض لتوليد غرامات
        try {
            java.lang.reflect.Field field = Loan.class.getDeclaredField("dueDate");
            field.setAccessible(true);
            field.set(l1, LocalDate.now().minusDays(29));
            field.set(l2, LocalDate.now().minusDays(8));
            field.set(l3, LocalDate.now().minusDays(30));
            field.set(l4, LocalDate.now().minusDays(8));
        } catch(Exception ignored){}

        // إضافة الغرامات تلقائياً
        if(l1.isOverdue()) fineService.addFine(u1, l1.getFineAmount());
        if(l2.isOverdue()) fineService.addFine(u2, l2.getFineAmount());
        if(l3.isOverdue()) fineService.addFine(u3, l3.getFineAmount());
        if(l4.isOverdue()) fineService.addFine(u3, l4.getFineAmount());

        // ======= Main Menu =======
        while(true){
            System.out.println("\n===== Library System =====");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as User");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");
            int choice = sc.nextInt(); sc.nextLine();

            if(choice==1){
                System.out.print("Admin username: "); 
                String name = sc.nextLine();
                System.out.print("Password: "); 
                String pass = sc.nextLine();

                if(name.equals(admin.getUsername()) && admin.checkPassword(pass)){
                    System.out.println("✅ Admin login successful!");
                    adminMenu(sc, books, cds, users, loanService, notifier);
                } else System.out.println("❌ Invalid username or password.");
            }
            else if(choice==2){
                System.out.print("Enter your name: "); String name = sc.nextLine();
                System.out.print("Enter your email: "); String email = sc.nextLine();

                User loggedUser = null;
                for(User u: users){
                    if(u.getName().equalsIgnoreCase(name) && u.getEmail().equalsIgnoreCase(email)){
                        loggedUser = u; break;
                    }
                }

                if(loggedUser!=null){
                    System.out.println("✅ Logged in as user: "+loggedUser.getName());
                    userMenu(sc, loggedUser, books, cds, loanService);
                } else System.out.println("❌ No user found with that info.");
            }
            else if(choice==3){ System.out.println("👋 Exiting system..."); break; }
            else System.out.println("❗ Invalid option.");
        }

        sc.close();
    }

    // ======= Admin Menu =======
    private static void adminMenu(Scanner sc, List<Book> books, List<CD> cds, List<User> users, LoanService loanService, MediaEmailNotifier notifier){
        while(true){
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. View all books");
            System.out.println("2. View all CDs");
            System.out.println("3. Add a book");
            System.out.println("4. Delete a book");
            System.out.println("5. Search book");
            System.out.println("6. View users and fines");
            System.out.println("7. Register a new user");
            System.out.println("8. View number of borrowed books");
            System.out.println("9. Send reminders");
            System.out.println("10. Unregister a user");
            System.out.println("11. Logout");

            System.out.print("Your choice: "); int c=sc.nextInt(); sc.nextLine();

            switch(c){
                case 1:
                    for(Book b: books){
                        System.out.println(b + (b.isBorrowed() ? " (Borrowed)" : " (Available)"));
                    }
                    break;
                case 2:
                    for(CD cd: cds){
                        System.out.println(cd + (cd.isBorrowed() ? " (Borrowed)" : " (Available)"));
                    }
                    break;
                case 3:
                    System.out.print("Enter title: "); String title=sc.nextLine();
                    System.out.print("Enter author: "); String author=sc.nextLine();
                    System.out.print("Enter ISBN: "); String isbn=sc.nextLine();
                    boolean exists=false;
                    for(Book b:books){ if(b.getIsbn().equals(isbn)){ exists=true; break; } }
                    if(exists) System.out.println("❌ ISBN already exists.");
                    else { books.add(new Book(title,author,isbn)); System.out.println("✅ Book added!"); }
                    break;
                case 4:
                    System.out.print("Enter ISBN to delete: "); String isbnDel=sc.nextLine();
                    Book del=null;
                    for(Book b: books){
                        if(b.getIsbn().equals(isbnDel)){
                            del=b; break;
                        }
                    }
                    if(del!=null){ books.remove(del); System.out.println("✅ Book deleted!"); }
                    else System.out.println("❌ Book not found.");
                    break;
                case 5:
                    System.out.println("Search by: 1-Title 2-Author 3-ISBN"); int s=sc.nextInt(); sc.nextLine();
                    System.out.print("Query: "); String q=sc.nextLine();
                    SearchStrategy strategy=null;
                    if(s==1) strategy=new SearchByTitle();
                    else if(s==2) strategy=new SearchByAuthor();
                    else if(s==3) strategy=new SearchByISBN();

                    if(strategy!=null){
                        List<Book> res=strategy.search(books,q);
                        if(res.isEmpty()) System.out.println("❌ No books found.");
                        else for(Book b: res) System.out.println(b);
                    } else System.out.println("❗ Invalid search option.");
                    break;
                case 6:
                    for(User u: users){
                        System.out.println(u.getName()+" | "+u.getEmail()+" | Fine: $"+u.getFineBalance());
                    }
                    break;
                case 7:
                    System.out.print("Enter new user name: "); String uname=sc.nextLine();
                    System.out.print("Enter email: "); String uemail=sc.nextLine();
                    users.add(new User(uname,uemail));
                    System.out.println("✅ User registered!");
                    break;
                case 8:
                    long countBooks=0, countCDs=0;
                    for(Book b: books) if(b.isBorrowed()) countBooks++;
                    for(CD cd: cds) if(cd.isBorrowed()) countCDs++;
                    System.out.println("📖 Borrowed books: "+countBooks+" | 💿 Borrowed CDs: "+countCDs);
                    break;
                case 9:
                    System.out.println("📩 Sending overdue reminders...");
                    for(User u: users){
                        notifier.sendOverdueEmail(u, loanService.getUserLoans(u));
                    }
                    break;
                case 10:
                    System.out.print("Enter email of user to unregister: "); 
                    String email = sc.nextLine();
                    User target=null;
                    for(User u: users){
                        if(u.getEmail().equalsIgnoreCase(email)){
                            target=u; break;
                        }
                    }
                    if(target==null) System.out.println("❌ No user found with that email.");
                    else if(target.getFineBalance() > 0) System.out.println("❌ Cannot unregister user with unpaid fines.");
                    else {
                        boolean hasActive=false;
                        for(Loan l: target.getLoans()){
                            if(!l.isReturned()){ hasActive=true; break; }
                        }
                        if(hasActive) System.out.println("❌ Cannot unregister user with active loans.");
                        else { users.remove(target); System.out.println("✅ User unregistered successfully: " + target.getName()); }
                    }
                    break;
                case 11:
                    System.out.println("🔙 Logout");
                    return;
                default:
                    System.out.println("❗ Invalid option.");
            }
        }
    }

    // ======= User Menu =======
    private static void userMenu(Scanner sc, User user, List<Book> books, List<CD> cds, LoanService loanService){
        while(true){
            System.out.println("\n===== User Menu =====");
            System.out.println("1. Show all books");
            System.out.println("2. Show all CDs");
            System.out.println("3. Search book");
            System.out.println("4. Borrow a book or CD");
            System.out.println("5. Return a book or CD");
            System.out.println("6. View & pay fine");
            System.out.println("7. Logout");

            System.out.print("Choice: "); int c=sc.nextInt(); sc.nextLine();

            switch(c){
                case 1:
                    for(Book b: books){
                        System.out.println(b + (b.isBorrowed() ? " (Borrowed)" : " (Available)"));
                    }
                    break;
                case 2:
                    for(CD cd: cds){
                        System.out.println(cd + (cd.isBorrowed() ? " (Borrowed)" : " (Available)"));
                    }
                    break;
                case 3:
                    System.out.println("Search by: 1-Title 2-Author 3-ISBN"); int s=sc.nextInt(); sc.nextLine();
                    System.out.print("Query: "); String q=sc.nextLine();
                    SearchStrategy strategy=null;
                    if(s==1) strategy=new SearchByTitle();
                    else if(s==2) strategy=new SearchByAuthor();
                    else if(s==3) strategy=new SearchByISBN();

                    if(strategy!=null){
                        List<Book> res=strategy.search(books,q);
                        if(res.isEmpty()) System.out.println("❌ No books found.");
                        else for(Book b: res) System.out.println(b);
                    } else System.out.println("❗ Invalid search option.");
                    break;
                case 4:
                    List<Loan> userLoans = loanService.getUserLoans(user);
                    boolean hasOverdue=false;
                    for(Loan l: userLoans){
                        if(l.isOverdue()){ hasOverdue=true; break; }
                    }
                    if(hasOverdue){
                        System.out.println("❌ You have overdue items. Please return them or pay fines first.");
                        break;
                    }

                    System.out.print("Enter title: "); String title=sc.nextLine();
                    Book bookFound=null;
                    for(Book b: books){
                        if(b.getTitle().equalsIgnoreCase(title) && !b.isBorrowed()){ bookFound=b; break; }
                    }
                    CD cdFound=null;
                    for(CD cd: cds){
                        if(cd.getTitle().equalsIgnoreCase(title) && !cd.isBorrowed()){ cdFound=cd; break; }
                    }

                    if(bookFound != null){
                        loanService.createLoan(bookFound,user);
                        System.out.println("✅ Borrowed Book! Due: " + bookFound.getDueDate());
                    } else if(cdFound != null){
                        loanService.createLoan(cdFound,user);
                        System.out.println("✅ Borrowed CD! Due: " + cdFound.getDueDate());
                    } else System.out.println("❌ Not available.");
                    break;
                case 5:
                    System.out.print("Enter title to return: "); String titleReturn=sc.nextLine();
                    Loan ret=null;
                    for(Loan l: loanService.getUserLoans(user)){
                        if(l.getMedia().getTitle().equalsIgnoreCase(titleReturn)){ ret=l; break; }
                    }
                    if(ret!=null){ loanService.returnLoan(ret); System.out.println("✅ Returned."); }
                    else System.out.println("❌ You didn't borrow this item.");
                    break;
                case 6:
                    System.out.println("Your fine: $"+user.getFineBalance());
                    if(user.getFineBalance()>0){
                        System.out.print("Pay now? (y/n): "); String ans=sc.nextLine();
                        if(ans.equalsIgnoreCase("y")){
                            System.out.print("Amount: "); double amt=sc.nextDouble(); sc.nextLine();
                            user.payFine(amt);
                            System.out.println("💰 Remaining: $"+user.getFineBalance());
                        }
                    }
                    break;
                case 7:
                    System.out.println("🔙 Logout");
                    return;
                default:
                    System.out.println("❗ Invalid option.");
            }
        }
    }
}

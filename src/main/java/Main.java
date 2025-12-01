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

        // ======= Default Users =======
        List<User> users = new ArrayList<>();
        User u1 = new User("Noor", "noorfayek321@gmail.com");
        User u2 = new User("hala", "hala@gmail.com");
        User u3 = new User("Ali", "ali@gmail.com");
        User u4 = new User("Sara", "sara@gmail.com");
        users.addAll(Arrays.asList(u1,u2,u3,u4));

        // ======= Services =======
        FineService fineService = new FineService();
        LoanService loanService = new LoanService();

        // ======= Borrow Books & Assign Fines Automatically =======
        Loan l1 = loanService.createLoan(b1, u1);
        try { 
            var field = Loan.class.getDeclaredField("dueDate"); 
            field.setAccessible(true);
            field.set(l1, LocalDate.now().minusDays(29)); 
        } catch(Exception ignored){}
        if(l1.isOverdue()) fineService.addFine(u1, 5.0);

        Loan l2 = loanService.createLoan(b2, u2);
        try { 
            var field = Loan.class.getDeclaredField("dueDate"); 
            field.setAccessible(true);
            field.set(l2, LocalDate.now().minusDays(30)); 
        } catch(Exception ignored){}
        if(l2.isOverdue()) fineService.addFine(u2, 5.0);

        loanService.createLoan(b3, u3);

        Admin admin = new Admin("admin", "1234");

        while(true){
            System.out.println("\n===== Library System =====");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as User");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");
            int choice = sc.nextInt(); sc.nextLine();

            if(choice==1){
                System.out.print("Admin username: "); String name = sc.nextLine();
                System.out.print("Password: "); String pass = sc.nextLine();
                if(name.equals(admin.getUsername()) && pass.equals(admin.getPassword())){
                    System.out.println("✅ Admin login successful!");
                    adminMenu(sc, books, users, loanService);
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
                    userMenu(sc, loggedUser, books, loanService);
                } else System.out.println("❌ No user found with that info.");
            }
            else if(choice==3){ System.out.println("👋 Exiting system..."); break; }
            else System.out.println("❗ Invalid option.");
        }

        sc.close();
    }

    private static void adminMenu(Scanner sc, List<Book> books, List<User> users, LoanService loanService){
        while(true){
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. View all books");
            System.out.println("2. Add a book");
            System.out.println("3. Delete a book");
            System.out.println("4. Search book");
            System.out.println("5. View users and fines");
            System.out.println("6. Register a new user");
            System.out.println("7. View number of borrowed books");
            System.out.println("8. Send reminder");
            System.out.println("9. Unregister a user"); // ✅ New option
            System.out.println("10. Logout");
         
            System.out.print("Your choice: "); int c=sc.nextInt(); sc.nextLine();

            switch(c){
                case 1 -> books.forEach(b->System.out.println(b+(b.isBorrowed()?" (Borrowed)":" (Available)")));
                case 2->{ System.out.print("Enter title: "); String title=sc.nextLine();
                          System.out.print("Enter author: "); String author=sc.nextLine();
                          System.out.print("Enter ISBN: "); String isbn=sc.nextLine();
                          boolean exists=false;
                          for(Book b:books){ if(b.getIsbn().equals(isbn)){ exists=true; break; } }
                          if(exists) System.out.println("❌ ISBN already exists.");
                          else { books.add(new Book(title,author,isbn)); System.out.println("✅ Book added!"); } }
                case 3->{ System.out.print("Enter ISBN to delete: "); String isbn=sc.nextLine();
                          Book del=books.stream().filter(b->b.getIsbn().equals(isbn)).findFirst().orElse(null);
                          if(del!=null){ books.remove(del); System.out.println("✅ Book deleted!"); }
                          else System.out.println("❌ Book not found."); }
                case 4->{ System.out.println("Search by: 1-Title 2-Author 3-ISBN"); int s=sc.nextInt(); sc.nextLine();
                          System.out.print("Query: "); String q=sc.nextLine();
                          SearchStrategy strategy=switch(s){ case 1->new SearchByTitle(); case 2->new SearchByAuthor(); case 3->new SearchByISBN(); default->null; };
                          if(strategy!=null){ List<Book> res=strategy.search(books,q); if(res.isEmpty()) System.out.println("❌ No books found."); else res.forEach(System.out::println);}
                          else System.out.println("❗ Invalid search option."); }
                case 5->users.forEach(u->System.out.println(u.getName()+" | "+u.getEmail()+" | Fine: $"+u.getFineBalance()));
                case 6->{ System.out.print("Enter new user name: "); String uname=sc.nextLine();
                          System.out.print("Enter email: "); String uemail=sc.nextLine();
                          users.add(new User(uname,uemail)); System.out.println("✅ User registered!"); }
                case 7->{ long count=books.stream().filter(Book::isBorrowed).count(); System.out.println("📖 Total borrowed books: "+count);}
                case 8->{ System.out.println("📩 Sending overdue reminders...");
                          Dotenv dotenv=Dotenv.load();
                          String emailUser=dotenv.get("EMAIL_USERNAME");
                          String emailPass=dotenv.get("EMAIL_PASSWORD");
                          EmailService emailService=new EmailService(emailUser,emailPass);
                          for(Loan l:loanService.getAllLoans()){
                              if(!l.isReturned() && l.isOverdue()){
                                  User borrower=l.getUser();
                                  String subj="📚 Overdue Book Reminder - Library";
                                  String body="Dear "+borrower.getName()+",\n\nThe book \""+l.getBook().getTitle()+"\" is overdue by "+l.isOverdue()+" day(s).\nPlease return it as soon as possible.\n\nLibrary System";
                                  emailService.sendEmail(borrower.getEmail(),subj,body);
                                  System.out.println("Reminder sent to "+borrower.getEmail());
                              }
                          } }
                
                case 9 -> { // ✅ Unregister user
                    System.out.print("Enter email of user to unregister: "); 
                    String email = sc.nextLine();
                    User target = users.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
                    if(target == null) System.out.println("❌ No user found with that email.");
                    else if(target.getFineBalance() > 0) System.out.println("❌ Cannot unregister user with unpaid fines.");
                    else if(target.getLoans().stream().anyMatch(l -> !l.isReturned())) System.out.println("❌ Cannot unregister user with active loans.");
                    else { users.remove(target); System.out.println("✅ User unregistered successfully: " + target.getName()); }
                }
                case 10-> { System.out.println("🔙 Logout"); return; }
                default->System.out.println("❗ Invalid option.");
            }
        }
    }

    
    
    
    private static void userMenu(Scanner sc, User user, List<Book> books, LoanService loanService){
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
                case 1->books.forEach(b->System.out.println(b+(b.isBorrowed()?" (Borrowed)":" (Available)")));
                case 2->{ System.out.println("Search by: 1-Title 2-Author 3-ISBN"); int s=sc.nextInt(); sc.nextLine();
                          System.out.print("Query: "); String q=sc.nextLine();
                          SearchStrategy strategy=switch(s){ case 1->new SearchByTitle(); case 2->new SearchByAuthor(); case 3->new SearchByISBN(); default->null; };
                          if(strategy!=null){ List<Book> res=strategy.search(books,q); if(res.isEmpty()) System.out.println("❌ No books found."); else res.forEach(System.out::println);}
                          else System.out.println("❗ Invalid search option."); }
                case 3->{ List<Loan> userLoans=loanService.getUserLoans(user);
                          if(!user.canBorrow() || userLoans.stream().anyMatch(Loan::isOverdue)){
                              System.out.println("❌ Pay your fine first or return overdue books. Fine: $"+user.getFineBalance()); break; }
                          System.out.print("Enter book title: "); String title=sc.nextLine();
                          Book found=books.stream().filter(b->b.getTitle().equalsIgnoreCase(title)&&!b.isBorrowed()).findFirst().orElse(null);
                          if(found!=null){ loanService.createLoan(found,user); System.out.println("✅ Borrowed! Due: "+found.getDueDate()); }
                          else System.out.println("❌ Book unavailable."); }
                case 4->{ System.out.print("Enter book title to return: "); String title=sc.nextLine();
                          Loan ret=loanService.getUserLoans(user).stream().filter(l->l.getBook().getTitle().equalsIgnoreCase(title)).findFirst().orElse(null);
                          if(ret!=null){ loanService.returnLoan(ret); System.out.println("✅ Book returned."); }
                          else System.out.println("❌ You didn't borrow this book."); }
                case 5->{ System.out.println("Your fine: $"+user.getFineBalance());
                          if(user.getFineBalance()>0){ System.out.print("Pay now? (y/n): "); String ans=sc.nextLine();
                              if(ans.equalsIgnoreCase("y")){ System.out.print("Amount: "); double amt=sc.nextDouble(); sc.nextLine(); user.payFine(amt); System.out.println("💰 Remaining: $"+user.getFineBalance()); } }
                }
                case 6-> { System.out.println("🔙 Logout"); return; }
                default->System.out.println("❗ Invalid option.");
            }
        }
    }
}
